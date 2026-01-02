package model.http;

import model.http.sub.HttpVersion;
import model.http.sub.RequestMethod;

import java.util.Map;

public class HttpStartLine {

    private final RequestMethod method;
    private String pathUrl;
    private final Map<String, Object> queryParameterList;
    private final HttpVersion version;

    private static final String INDEX_FILE_NAME = "/index.html";

    public HttpStartLine(RequestMethod method, String pathUrl, Map<String, Object> queryParameterList, HttpVersion version) {
        this.method = method;
        this.pathUrl = pathUrl;
        this.queryParameterList = queryParameterList;
        this.version = version;
    }

    public HttpStartLine(String method, String pathUrl, Map<String, Object> queryParameterList, String version) {
        this.method = RequestMethod.findByType(method);
        this.pathUrl = pathUrl;
        this.queryParameterList = queryParameterList;
        this.version = HttpVersion.findByType(version);
    }

    public void addIndexHtml() {
        this.pathUrl = this.pathUrl.concat(INDEX_FILE_NAME);
    }

    public RequestMethod getMethod() {
        return method;
    }

    public String getPathUrl() {
        return pathUrl;
    }

    public Map<String, Object> getQueryParameterList() {
        return queryParameterList;
    }

    public HttpVersion getVersion() {
        return version;
    }
}
