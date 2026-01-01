package webserver.handler.response;

import model.http.TotalHttpMessage;
import webserver.handler.response.impl.StaticResourceResponseHandler;
import webserver.handler.response.impl.DynamicResourceResponseHandler;

import java.io.OutputStream;
import java.util.Arrays;

public enum RequestResourceType {
    // 깨달은 점 : Enum은 GC의 대상이 되지 않으므로 실행 이후 쭉 살아 있게 된다.
    // 이때 Enum이 갖고 있는 멤버 변수 또한 GC의 대상이 되지 않으므로 굳이 싱글턴을 적용하지 않더라도 자동으로
    // 싱글턴이 적용이 된다. 무작정 싱글턴을 적용하는 건 코드 길이만 늘리게 되어 안 좋을 수도 있다는 것을 깨달았다.
    DYNAMIC(new DynamicResourceResponseHandler()),
    STATIC(new StaticResourceResponseHandler());

    private final ResponseHandler handler;

    RequestResourceType(ResponseHandler handler) {
        this.handler = handler;
    }

    public static boolean process(OutputStream out, TotalHttpMessage message) {
        return Arrays.stream(RequestResourceType.values())
                .anyMatch(type -> type.handler.sendResponse(out, message));
    }
}
