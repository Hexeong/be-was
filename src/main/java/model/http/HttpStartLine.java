package model.http;

import model.http.sub.HttpVersion;
import model.http.sub.RequestMethod;

public record HttpStartLine(
        RequestMethod method,
        String url,
        HttpVersion version
) {
    public HttpStartLine(String method, String url, String version) {
        this(
                RequestMethod.findByType(method),
                url,
                HttpVersion.findByType(version)
        );
    }
}
