package routing;

import business.BusinessHandler;
import model.http.HttpRequest;
import routing.user.UserRouter;
import java.util.Arrays;
import java.util.List;

public enum TotalRouteMapping {
    USER("/user", new UserRouter()),
    DEFAULT("/", new DefaultRouter());

    private final String basePath;
    private final DomainRouter router;

    TotalRouteMapping(String basePath, DomainRouter router) {
        this.basePath = basePath;
        this.router = router;
    }

    public static BusinessHandler route(HttpRequest req) {
        String pathUrl = req.line().getPathUrl();

        List<BusinessHandler> mappedHandlerList =  Arrays.stream(values())
                .filter(mapping -> pathUrl.startsWith(mapping.basePath))
                .map(mapping -> mapping.router.getHandler(req)).toList();

        for (BusinessHandler handler : mappedHandlerList) {
            if (handler != null)
                return handler;
        }
        return null;
    }
}
