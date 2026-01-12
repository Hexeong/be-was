package resolver.argument;

import model.http.HttpRequest;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class FormDataArgumentResolver implements ArgumentResolver {

    private static final String DATA_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

    @Override
    public boolean supports(Parameter parameter) {
        // 기본 타입(String, int 등)이나 HttpRequest 등이 아닌, 개발자가 만든 커스텀 객체(Entity, DTO 등)인 경우 처리한다고 가정
        Class<?> type = parameter.getType();
        return !type.isPrimitive()
                && !type.getName().startsWith("java.")
                && !type.getName().startsWith("javax.")
                && !type.equals(HttpRequest.class);
    }

    @Override
    public Object resolve(Parameter parameter, HttpRequest request) {
        String bodyText = request.bodyText();

        Map<String, String> dataMap = parseBody(bodyText);

        return bindData(parameter.getType(), dataMap);
    }

    private Map<String, String> parseBody(String bodyText) {
        if (bodyText == null || bodyText.isBlank()) {
            return Map.of();
        }

        return Arrays.stream(bodyText.split(DATA_DELIMITER))
                .map(token -> token.split(KEY_VALUE_DELIMITER, 2)) // 값에 '='가 포함될 경우 대비
                .filter(arr -> arr.length == 2)
                .collect(Collectors.toMap(
                        arr -> decode(arr[0]),
                        arr -> decode(arr[1]),
                        (oldVal, newVal) -> oldVal // 중복 키 발생 시 기존 값 유지
                ));
    }

    // [AI] 리플렉션을 사용하여 Map의 데이터를 객체 필드에 주입
    private <T> T bindData(Class<T> clazz, Map<String, String> dataMap) {
        try {
            // 1. 기본 생성자로 인스턴스 생성
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            T instance = constructor.newInstance();

            // 2. 클래스의 모든 필드를 순회하며 값 주입
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true); // private 필드 접근 허용
                String paramName = field.getName();

                // 3. Map에 해당 필드명의 데이터가 있으면 주입
                if (dataMap.containsKey(paramName)) {
                    String value = dataMap.get(paramName);
                    // String 타입 필드만 지원 (필요 시 int, boolean 변환 로직 추가 가능)
                    field.set(instance, value);
                }
            }
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("데이터 바인딩 실패: " + clazz.getSimpleName(), e);
        }
    }

    private String decode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return value;
        }
    }
}