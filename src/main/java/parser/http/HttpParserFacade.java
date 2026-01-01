package parser.http;

import model.http.HttpBody;
import model.http.HttpStartLine;
import model.http.TotalHttpMessage;
import parser.http.impl.HttpParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class HttpParserFacade {
    private final HeadParser<HttpStartLine> startLineParser;
    private final HeadParser<Map<String, String>> headerParser;
    private final ContentParser<HttpBody> bodyParser;

    private static volatile HttpParserFacade instance = null;

    private HttpParserFacade() {
        HttpParserFactory factory = new HttpParserFactory();
        this.startLineParser = factory.getStartLineParser();
        this.headerParser = factory.getHeaderParser();
        this.bodyParser = factory.getBodyParser();
    }

    public static HttpParserFacade getInstance() {
        if (instance == null) {
            synchronized (HttpParserFacade.class) {
                if (instance == null) {
                    instance = new HttpParserFacade();
                }
            }
        }
        return instance;
    }

    public TotalHttpMessage parse(InputStream in) throws IOException {
        InputStreamReader isr = new InputStreamReader(in);
        BufferedReader bufRed = new BufferedReader(isr);

        HttpStartLine startLine = startLineParser.parse(bufRed);
        Map<String, String> header = headerParser.parse(bufRed);
        HttpBody body = bodyParser.parse(bufRed, header);

        return new TotalHttpMessage(
                startLine,
                header,
                body
        );
    }
}
