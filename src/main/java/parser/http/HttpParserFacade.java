package parser.http;

import model.http.HttpBody;
import model.http.HttpStartLine;
import model.http.TotalHttpMessage;
import parser.http.impl.HttpBodyParser;
import parser.http.impl.HttpHeaderParser;
import parser.http.impl.HttpStartLineParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpParserFacade {

    private HttpParserFacade() {}

    public static TotalHttpMessage parse(InputStream in) throws IOException {
        // 여기서는 안전하게 UTF-8으로 읽고, 깨진 문자에 대해 정규표현식으로 검사.
        // Body부분에서 한글이 들어올 수 있기 때문에, Header 파싱에 대해서만 US-ASCII에서 허용되지 않는 문자를 검출한다.
        InputStreamReader isr = new InputStreamReader(in, StandardCharsets.UTF_8);
        BufferedReader bufRed = new BufferedReader(isr);

        HttpStartLine startLine = HttpStartLineParser.parse(bufRed);
        Map<String, String> header = HttpHeaderParser.parse(bufRed);
        HttpBody body = HttpBodyParser.parse(bufRed, header);

        return new TotalHttpMessage(
                startLine,
                header,
                body
        );
    }
}
