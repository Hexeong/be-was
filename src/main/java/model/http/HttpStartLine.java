package model.http;

import model.http.sub.HttpVersion;
import model.http.sub.RequestMethod;

import java.util.Map;

public record HttpStartLine(
        RequestMethod method,
        String pathUrl,
        Map<String, Object> queryParameterList,
        HttpVersion version
) {
    public HttpStartLine(String method, String pathUrl, Map<String, Object> queryParameterList, String version) {
        this(
                RequestMethod.findByType(method),
                pathUrl,
                queryParameterList,
                HttpVersion.findByType(version)
        );
    }
}
