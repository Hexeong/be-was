package extractor.http;

import java.util.*;

public class QueryParameterExtractor implements HttpInfoExtractor<Map<String, Object>> {

    private static volatile QueryParameterExtractor instance = null;

    private QueryParameterExtractor() {}

    public static QueryParameterExtractor getInstance() {
        if (instance == null) {
            synchronized (QueryParameterExtractor.class) {
                if (instance == null) {
                    instance = new QueryParameterExtractor();
                }
            }
        }
        return instance;
    }

    // [AI] 웹 표준 검색 및 처리 방식 검색
    public Map<String, Object> extract(String queryString) {
        Map<String, Object> queryParams = new HashMap<>();

        // 방어로직 추가
        if (queryString == null || queryString.isEmpty()) {
            return queryParams;
        }

        String[] pairs = queryString.split("&");

        for (String pair : pairs) {
            String[] kv = pair.split("=", 2); // 최대 2개로만 쪼갬 (값 안에 =가 있을 경우 대비)

            String key = kv[0];
            String value = kv.length > 1 ? kv[1] : "";

            // 웹 표준에서 배열을 보낼 떄의 약속은 같은 키를 여러 번 바복하는 것이기에 다음과 같이 구현
            if (queryParams.containsKey(key)) {
                Object existingValue = queryParams.get(key);

                if (existingValue instanceof List) {
                    ((List<String>) existingValue).add(value);
                }
                else {
                    List<String> values = new ArrayList<>();
                    values.add((String) existingValue); // 기존 값
                    values.add(value);                  // 새 값
                    queryParams.put(key, values);
                }
            } else {
                queryParams.put(key, value);
            }
        }

        return queryParams;
    }
}
