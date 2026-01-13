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

            if (queryParameterList != null && !queryParameterList.isEmpty()) {
                sb.append("?");
                sb.append(generateQueryString(queryParameterList));
            }

            sb.append(" ").append(version.getVersion()).append("\r\n");

            // Chunked 여부 확인
            // 대소문자 무시 체크 ("Transfer-Encoding" 키가 대소문자가 다를 수 있으므로 루프 대신 간단히 처리하거나,
            // 실제로는 Header Map키를 정규화해서 관리하는 것이 좋음. 여기선 로직 유지)
            boolean isChunked = false;
            if (headers != null) {
                // 헤더 키 대소문자 무시 검색
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    if ("Transfer-Encoding".equalsIgnoreCase(entry.getKey()) && "chunked".equalsIgnoreCase(entry.getValue())) {
                        isChunked = true;
                        break;
                    }
                }
            }

            // 2. Headers
            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    sb.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
                }
            }

            // Chunked가 아닐 때만 Content-Length 자동 계산
            if (!isChunked && body != null && !body.isEmpty()) {
                if (headers == null || !headers.containsKey("Content-Length")) {
                    int contentLength = body.getBytes("UTF-8").length;
                    sb.append("Content-Length: ").append(contentLength).append("\r\n");
                }
            }

            sb.append("\r\n"); // Header End

            // 3. Body (Chunked 인코딩 적용 로직)
            if (body != null && !body.isEmpty()) {
                if (isChunked) {
                    sb.append(createChunkedBody(body));
                } else {
                    sb.append(body);
                }
            }

            return new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Encoding not supported: UTF-8", e);
        } catch (Exception e) {
            throw new RuntimeException("Error creating test fixture", e);
        }
    }

    // [수정] NIO 제거 버전: 평문 Body를 Chunked 포맷으로 변환
    private static String createChunkedBody(String originalBody) throws UnsupportedEncodingException {
        StringBuilder chunkedSb = new StringBuilder();

        // NIO 제거: StandardCharsets.UTF_8 -> "UTF-8"
        byte[] bytes = originalBody.getBytes("UTF-8");

        // 반으로 쪼개기
        int splitIndex = bytes.length / 2;
        if (splitIndex == 0) splitIndex = bytes.length;

        // First Chunk
        byte[] part1 = new byte[splitIndex];
        System.arraycopy(bytes, 0, part1, 0, splitIndex);

        chunkedSb.append(Integer.toHexString(part1.length)).append("\r\n");
        // NIO 제거: "UTF-8" 문자열 사용
        chunkedSb.append(new String(part1, "UTF-8")).append("\r\n");

        // Second Chunk
        if (splitIndex < bytes.length) {
            int part2Len = bytes.length - splitIndex;
            byte[] part2 = new byte[part2Len];
            System.arraycopy(bytes, splitIndex, part2, 0, part2Len);

            chunkedSb.append(Integer.toHexString(part2.length)).append("\r\n");
            // NIO 제거: "UTF-8" 문자열 사용
            chunkedSb.append(new String(part2, "UTF-8")).append("\r\n");
        }

        // End Chunk
        chunkedSb.append("0\r\n\r\n");

        return chunkedSb.toString();
    }

    private static String generateQueryString(Map<String, Object> params) {
        StringBuilder query = new StringBuilder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Collection<?>) {
                for (Object item : (Collection<?>) value) {
                    query.append(key).append("=").append(item).append("&");
                }
            } else {
                query.append(key).append("=").append(value).append("&");
            }
        }
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
