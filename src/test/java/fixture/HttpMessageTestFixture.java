package fixture;

import model.http.HttpStartLine;
import model.http.HttpRequest;
import model.http.sub.HttpVersion;
import model.http.sub.RequestMethod;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Map;

public class HttpMessageTestFixture {
    public HttpMessageTestFixture() {}

    public static InputStream createRawByteHttpMessage(RequestMethod method,
                                                       String pathUrl,
                                                       Map<String, Object> queryParameterList,
                                                       HttpVersion version,
                                                       Map<String, String> headers,
                                                       String body) {

        try {
            StringBuilder sb = new StringBuilder();

            // 1. Start Line
            sb.append(method.name()).append(" ");
            sb.append(pathUrl);

            // 쿼리 파라미터 생성 로직을 별도 메서드로 분리하여 정교하게 처리
            if (queryParameterList != null && !queryParameterList.isEmpty()) {
                sb.append("?");
                sb.append(generateQueryString(queryParameterList));
            }

            sb.append(" ").append(version.getVersion()).append("\r\n");

            // 2. Headers
            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    sb.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
                }
            }

            // Body 길이 계산 (UTF-8 문자열 사용)
            if (body != null && !body.isEmpty() && (headers == null || !headers.containsKey("Content-Length"))) {
                int contentLength = body.getBytes("UTF-8").length;
                sb.append("Content-Length: ").append(contentLength).append("\r\n");
            }

            sb.append("\r\n");

            // 4. Body
            if (body != null) {
                sb.append(body);
            }

            return new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Encoding not supported", e);
        }
    }

    // [핵심] 리스트 처리를 위한 쿼리 스트링 생성 메서드
    private static String generateQueryString(Map<String, Object> params) {
        StringBuilder query = new StringBuilder();

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // 1. 만약 값이 리스트(Collection)라면? -> id=1&id=2&id=3 형태로 풂
            if (value instanceof Collection<?>) {
                for (Object item : (Collection<?>) value) {
                    query.append(key).append("=").append(item).append("&");
                }
            }
            // 2. 그냥 단일 값이라면? -> id=1 형태로
            else {
                query.append(key).append("=").append(value).append("&");
            }
        }

        // 마지막에 붙은 '&' 제거
        if (query.length() > 0) {
            query.setLength(query.length() - 1);
        }

        return query.toString();
    }

    public static HttpRequest createParsedHttpMessage(RequestMethod method,
                                                      String pathUrl,
                                                      Map<String, Object> queryParameterList,
                                                      HttpVersion version,
                                                      Map<String, String> headers, String body) {

        return new HttpRequest(
                new HttpStartLine(method, pathUrl, queryParameterList, version),
                headers,
                body);
    }
}
