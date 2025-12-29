package model;

import java.util.HashMap;

public class ParsedHttpMessage {

    private String method;

    private String urlPath;

    private HashMap<String, String> headers;

    private String body;

    public ParsedHttpMessage(String method, String urlPath, HashMap<String, String> headers, String body) {
        this.method = method;
        this.urlPath = urlPath;
        this.headers = headers;
        this.body = body;
    }

    public String getUrlPath() {
        return urlPath;
    }

    @Override
    public String toString() {
        return """
                method: %s
                
                url_path: %s
                
                headers: %s
                
                body: %s
                """
                .formatted(method, urlPath, headers.toString(), body);
    }
}
