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
import java.util.Map;

public class HttpParserFacade {

    private HttpParserFacade() {
    }

    public static TotalHttpMessage parse(InputStream in) throws IOException {
        InputStreamReader isr = new InputStreamReader(in);
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
