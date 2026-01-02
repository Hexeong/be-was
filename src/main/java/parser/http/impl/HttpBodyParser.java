package parser.http.impl;

import model.http.HttpBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class HttpBodyParser {

    public HttpBodyParser() {}

    private static final String CONTENT_LENGTH_KEY = "content-length";

    // 고민: 인터페이스 분리 없이 Body와 같은 경우 header 중 Content-Length 값을 매개변수로 전달하여 parsing해야 하는데 어떻게 해야할까?
    // 해결: [AI] 그런거 없다고 함. 이미 입력값과 출력값 둘다 다른 상황에서 다른 인터페이스를 사용하는 건 무리임. ISP 원칙을 지키자.
    public static HttpBody parse(BufferedReader bufRed, Map<String, String> headers) throws IOException {
        if (headers.containsKey(CONTENT_LENGTH_KEY)) {
            int contentLength = Integer.parseInt(headers.get(CONTENT_LENGTH_KEY));
            char[] bodyChars = new char[contentLength];

            bufRed.read(bodyChars, 0, contentLength);

            return new HttpBody(new String(bodyChars));
        }
        return new HttpBody("");
    }
}
