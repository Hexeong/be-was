package parser.http.impl;

import model.http.HttpBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class HttpBodyParser {

    private HttpBodyParser() {}

    private static final String CONTENT_LENGTH_KEY = "content-length";

    public static HttpBody parse(BufferedReader bufRed, Map<String, String> headers) throws IOException {
        // [RFC 9112 Section 6.1]
        // TODO:: Transfer-Encoding 헤더가 있으면 Content-Length는 무조건 무시해야 함.
        //        만약 둘 다 있는데 Transfer-Encoding이 chunked가 아니라면 에러를 뱉거나 연결을 닫는 것이 원칙

        if (headers.containsKey(CONTENT_LENGTH_KEY)) {
            return parseFixedLengthBody(bufRed, headers);
        }

        // [RFC 9112 Section 6.3]
        // 요청(Request)에서 Content-Length도 없고 Transfer-Encoding도 없으면 Body 길이는 0이다.
        return new HttpBody("");
    }

    private static HttpBody parseFixedLengthBody(BufferedReader bufRed, Map<String, String> headers) throws IOException {
        try {
            int contentLength = Integer.parseInt(headers.get(CONTENT_LENGTH_KEY));
            char[] bodyChars = new char[contentLength];

            // TCP의 경우 Body 길이가 길어져 패킷 전체가 MTU보다 커질 경우, 패킷 분할이 진행된다.
            // 때문에, 한번에 최대 길이 만큼 안들어올 수 있어 content-length가 채워질 때까지 while문으로 read요청을 해야 한다.
            int offset = 0;
            while (offset < contentLength) {
                int read = bufRed.read(bodyChars, offset, contentLength - offset);
                if (read == -1) throw new IOException("Unexpected End of Stream in Fixed-Length Body");
                offset += read;
            }
            return new HttpBody(new String(bodyChars));
        } catch (NumberFormatException e) {
            throw new IOException("Invalid Content-Length format", e);
        }
    }
}
