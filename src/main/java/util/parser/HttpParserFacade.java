package util.parser;

import model.http.HttpStartLine;
import model.http.HttpRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class HttpParserFacade {

    private HttpParserFacade() {}

    public static HttpRequest parse(InputStream in) throws IOException {
        HttpStartLine startLine = HttpStartLineParser.parse(in);
        Map<String, String> header = HttpHeaderParser.parse(in);
        String bodyText = HttpBodyParser.parse(in, header);

        return new HttpRequest(
                startLine,
                header,
                bodyText
        );
    }
}
