package model.http.sub;

public enum RequestMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    PATCH("PATCH"),
    DELETE("DELETE");

    private final String code;

    RequestMethod(String code) {
        this.code = code;
    }

    public static RequestMethod findByType(String type) {
        for (RequestMethod value : RequestMethod.values()) {
            if (value.code.equals(type))
                return value;
        }
        throw new IllegalArgumentException("Not found RequestMethod By " + type);
    }
}
