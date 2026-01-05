package resolver;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class FormDataResolver implements ArgumentResolver<Map<String, String>> {
    private static final String DATA_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

    private static FormDataResolver instance = null;

    public static FormDataResolver getInstance() {
        if (instance == null) {
            synchronized (FormDataResolver.class) {
                if (instance == null) {
                    instance = new FormDataResolver();
                }
            }
        }
        return instance;
    }

    // [AI로 스트림 작성]
    public Map<String, String> resolve(String bodyText) {
        if (bodyText == null || bodyText.isBlank()) {
            return Map.of();
        }

        return Arrays.stream(bodyText.split(DATA_DELIMITER))
                .map(token -> token.split(KEY_VALUE_DELIMITER, 2)) // [중요] 값에 '='가 포함될 경우 대비
                .filter(arr -> arr.length == 2) // key=value 형식이 깨진 데이터 필터링
                .collect(Collectors.toMap(
                        arr -> decode(arr[0]),     // Key 디코딩
                        arr -> decode(arr[1]),     // Value 디코딩
                        (oldVal, newVal) -> oldVal // (선택) 중복된 키가 오면 기존 값 유지
                ));
    }

    // URL 인코딩된 문자(예: %EB%B0%95)를 원래 문자로 변환
    private static String decode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return value;
        }
    }
}
