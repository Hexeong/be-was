package routing;

import model.http.TotalHttpMessage;
import routing.user.UserRouter;
import writer.file.StaticResourceType;

import java.io.OutputStream;
import java.util.Arrays;

public enum TotalRouteMapping {
    USER("/user", new UserRouter());

    private final String basePath;
    private final DomainRouter router;

    TotalRouteMapping(String basePath, DomainRouter router) {
        this.basePath = basePath;
        this.router = router;
    }

    public static boolean route(OutputStream out, TotalHttpMessage totalHttpMessage) {
        String pathUrl = totalHttpMessage.line().getPathUrl();

        // [AI]로 stream 처리 최적화
        boolean isHandled = Arrays.stream(values())
                .filter(mapping -> pathUrl.startsWith(mapping.basePath))
                .anyMatch(mapping -> mapping.router.route(out, totalHttpMessage));

        // 맞는 게 없고 파일 확장자가 없다면, /index.html을 붙여 정적 리소스를 원하는 것인지 체크해봐야 한다.
        if (!isHandled && !StaticResourceType.isStaticResourceByUrl(pathUrl)) {
            totalHttpMessage.line().addIndexHtml();
        }
        return isHandled;
    }
}
