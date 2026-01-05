package model.http;

public class HttpBody {
    private final String bodyText;
    private Object parsedBody;

    public HttpBody(String bodyText) {
        this.bodyText = bodyText;
    }

    public String getBodyText() {
        return bodyText;
    }

    public <T> T getParsedBody(Class<T> type) {
        if (parsedBody == null) {
            return null;
        }
        return type.cast(parsedBody);
    }

    public void setParsedBody(Object parsedBody) {
        this.parsedBody = parsedBody;
    }
}
