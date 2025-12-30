package webserver.handler.response;

import webserver.handler.response.impl.StaticResourceResponseHandler;
import webserver.handler.response.impl.DynamicResourceResponseHandler;

public enum RequestResourceType {
    // 깨달은 점 : Enum은 GC의 대상이 되지 않으므로 실행 이후 쭉 살아 있게 된다.
    // 이때 Enum이 갖고 있는 멤버 변수 또한 GC의 대상이 되지 않으므로 굳이 싱글턴을 적용하지 않더라도 자동으로
    // 싱글턴이 적용이 된다. 무작정 싱글턴을 적용하는 건 코드 길이만 늘리게 되어 안 좋을 수도 있다는 것을 깨달았다.
    STATIC("static", new StaticResourceResponseHandler()),
    DYNAMIC("dynamic", new DynamicResourceResponseHandler());

    private final String code;
    private final ResponseHandler handler;

    RequestResourceType(String code, ResponseHandler handler) {
        this.code = code;
        this.handler = handler;
    }

    public ResponseHandler getHandler() {
        return handler;
    }

    public static RequestResourceType findByRequestResourceType(String type) {
        for (RequestResourceType resourceType : values()) {
            if (resourceType.code.equals(type)) {
                return resourceType;
            }
        }
        throw new IllegalArgumentException("Not Found type By " + type);
    }
}
