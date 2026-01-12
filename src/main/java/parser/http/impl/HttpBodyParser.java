package parser.http.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpBodyParser {

    private HttpBodyParser() {}

    private static final String CONTENT_LENGTH_KEY = "content-length";

    public static String parse(InputStream in, Map<String, String> headers) throws IOException {
        // [RFC 9112 Section 6.1]
        // TODO:: Transfer-Encoding 헤더가 있으면 Content-Length는 무조건 무시해야 함.
        //        만약 둘 다 있는데 Transfer-Encoding이 chunked가 아니라면 에러를 뱉거나 연결을 닫는 것이 원칙

        if (headers.containsKey(CONTENT_LENGTH_KEY)) {
            return parseFixedLengthBody(in, headers);
        }

        // [RFC 9112 Section 6.3]
        // 요청(Request)에서 Content-Length도 없고 Transfer-Encoding도 없으면 Body 길이는 0이다.
        return "";
    }

    private static String parseFixedLengthBody(InputStream inputStream, Map<String, String> headers) throws IOException {
        try {
            int contentLength = Integer.parseInt(headers.get(CONTENT_LENGTH_KEY));

            // 1. 바이트 배열 생성 (char[] 아님)
            byte[] bodyBytes = new byte[contentLength];

            int offset = 0;
            while (offset < contentLength) {
                // 2. 바이트 단위로 읽기
                int read = inputStream.read(bodyBytes, offset, contentLength - offset);
                if (read == -1) throw new IOException("Unexpected End of Stream in Fixed-Length Body");
                offset += read;
            }

            // 3. 바이트를 다 읽은 후 문자열로 변환 (여기서 인코딩 지정)
            return new String(bodyBytes, StandardCharsets.UTF_8);

        } catch (NumberFormatException e) {
            throw new IOException("Invalid Content-Length format", e);
        }
    }
}
