package model.http;

import java.util.Map;

public record HttpRequest(
        HttpStartLine line,
        Map<String, String> headers,
        HttpBody body
) {
    public HttpRequest() {
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
                .formatted(line.getMethod(), line.getPathUrl(), headers.toString(), body);
    }
}
