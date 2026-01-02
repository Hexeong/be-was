package model.http.sub;

public enum HttpVersion {
    HTTP_1_1("HTTP/1.1"),
    HTTP_1_2("HTTP/1.2"); // HTTP/2 이상부터는 TLS/SSL을 활용한 암호화가 필요하기에 나중에 추가 확장시 추가

    private final String version;

    HttpVersion(String version) {
        this.version = version;
    }

    public static HttpVersion findByType(String type) {
        for (HttpVersion value : HttpVersion.values()) {
            if (value.version.equals(type))
                return value;
        }
        throw new IllegalArgumentException("Not found HttpVersion By " + type);
    }

    public String getVersion() {
        return version;
    }
}
