package parser.http.impl;

public class HttpParserFactory {

    private final HttpStartLineParser startLineParser = new HttpStartLineParser();
    private final HttpHeaderParser headerParser = new HttpHeaderParser();
    private final HttpBodyParser bodyParser = new HttpBodyParser();

    public HttpParserFactory() {}

    public HttpStartLineParser getStartLineParser() {
        return startLineParser;
    }

    public HttpHeaderParser getHeaderParser() {
        return headerParser;
    }

    public HttpBodyParser getBodyParser() {
        return bodyParser;
    }
}
