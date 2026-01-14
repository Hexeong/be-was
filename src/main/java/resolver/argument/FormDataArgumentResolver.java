package resolver.argument;

import annotation.Formdata;
import annotation.MultipartFormdata;
import model.MultipartFile;
import model.http.HttpRequest;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class FormDataArgumentResolver implements ArgumentResolver {

    private static final String DATA_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(Formdata.class)
                || parameter.isAnnotationPresent(MultipartFormdata.class);
    }

    @Override
    public Object resolve(Parameter parameter, HttpRequest request) {
        String bodyText = request.bodyText();
        Map<String, Object> dataMap; // String 뿐만 아니라 MultipartFile도 담기 위해 Object로 변경

        if (parameter.isAnnotationPresent(MultipartFormdata.class)) {
            dataMap = parseMultipart(bodyText, request.headers().get("content-type"));
        } else {
            dataMap = parseUrlEncoded(bodyText);
        }

        return bindData(parameter.getType(), dataMap);
    }

    // URL Encoded는 String 값만 가짐
    private Map<String, Object> parseUrlEncoded(String bodyText) {
        if (bodyText == null || bodyText.isBlank()) return Map.of();

        return Arrays.stream(bodyText.split(DATA_DELIMITER))
                .map(token -> token.split(KEY_VALUE_DELIMITER, 2))
                .filter(arr -> arr.length == 2)
                .collect(Collectors.toMap(
                        arr -> decode(arr[0]),
                        arr -> decode(arr[1]),
                        (oldVal, newVal) -> oldVal
                ));
    }

    private Map<String, Object> parseMultipart(String bodyText, String contentType) {
        if (bodyText == null || bodyText.isBlank()) return Map.of();

        Map<String, Object> dataMap = new HashMap<>();
        String boundary = "--" + contentType.substring(contentType.indexOf("boundary=") + 9);
        String[] parts = bodyText.split(boundary);

        for (String part : parts) {
            if (part.isBlank() || part.trim().equals("--")) continue;

            int headerEndIndex = part.indexOf("\r\n\r\n");
            if (headerEndIndex == -1) continue;

            String headers = part.substring(0, headerEndIndex);
            String content = part.substring(headerEndIndex + 4);

            if (content.endsWith("\r\n")) {
                content = content.substring(0, content.length() - 2);
            }

            String name = extractHeaderValue(headers, "name=\"");

            if (headers.contains("filename=\"")) {
                // 파일명 추출
                String filename = extractHeaderValue(headers, "filename=\"");

                // 파일명이 비어있거나(null or empty), 내용이 없으면 객체를 생성하지 않음 (Skip)
                if (filename == null || filename.isBlank()) {
                    continue;
                }

                byte[] fileBytes = content.getBytes(StandardCharsets.ISO_8859_1);

                // 파일이 진짜 있을 때만 생성
                MultipartFile multipartFile = new MultipartFile(filename, contentType, fileBytes);
                dataMap.put(name, multipartFile);

            } else {
                // 일반 필드
                byte[] rawBytes = content.getBytes(StandardCharsets.ISO_8859_1);
                String utf8Content = new String(rawBytes, StandardCharsets.UTF_8);
                dataMap.put(name, utf8Content);
            }
        }
        return dataMap;
    }

    // 헤더 값 추출 헬퍼
    private String extractHeaderValue(String headers, String key) {
        int startIndex = headers.indexOf(key);
        if (startIndex == -1) return null;
        String value = headers.substring(startIndex + key.length());
        return value.substring(0, value.indexOf("\""));
    }

    private <T> T bindData(Class<T> clazz, Map<String, Object> dataMap) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            T instance = constructor.newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                String paramName = field.getName();

                if (dataMap.containsKey(paramName)) {
                    Object value = dataMap.get(paramName);

                    // 타입 변환 및 주입
                    if (field.getType() == MultipartFile.class && value instanceof MultipartFile) {
                        field.set(instance, value);
                    } else if (value instanceof String) {
                        field.set(instance, convertValue(field.getType(), (String) value));
                    }
                }
            }
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("데이터 바인딩 실패: " + clazz.getSimpleName(), e);
        }
    }

    private Object convertValue(Class<?> type, String value) {
        if (type == int.class || type == Integer.class) return Integer.parseInt(value);
        if (type == long.class || type == Long.class) return Long.parseLong(value);
        return value;
    }

    private String decode(String value) {
        try { return URLDecoder.decode(value, StandardCharsets.UTF_8); }
        catch (Exception e) { return value; }
    }
}