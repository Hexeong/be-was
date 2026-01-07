package parser.http;

import model.http.HttpBody;
import model.http.HttpStartLine;
import model.http.TotalHttpMessage;
import parser.http.impl.HttpBodyParser;
import parser.http.impl.HttpHeaderParser;
import parser.http.impl.HttpStartLineParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class HttpParserFacade {

    private HttpParserFacade() {}

    public static TotalHttpMessage parse(InputStream in) throws IOException {
        HttpStartLine startLine = HttpStartLineParser.parse(in);
        Map<String, String> header = HttpHeaderParser.parse(in);
        HttpBody body = HttpBodyParser.parse(in, header);

        return new TotalHttpMessage(
                startLine,
                header,
                body
        );
    }
}
