package model.http;

public record TotalHttpMessage(
        HttpStartLine line,
        HttpHeader headers,
        HttpBody body
) {
    public TotalHttpMessage() {
        this(null, null, null);
    }

    @Override
    public String toString() {
        return """
                method: %s
                
                url_path: %s
                
                headers: %s
                
                body: %s
                """
                .formatted(line.method(), line.url(), headers.toString(), body);
    }
}
