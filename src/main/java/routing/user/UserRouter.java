package routing.user;

import business.BusinessHandler;
import business.UserBusinessLogic;
import model.http.HttpRequest;
import model.http.sub.RequestMethod;
import resolver.argument.ArgumentResolver;
import resolver.argument.FormDataResolver;
import routing.DomainRouter;

import java.util.HashMap;
import java.util.Map;

public class UserRouter implements DomainRouter {
    private final Map<String, BusinessHandler> businessMap = new HashMap<>();

    public UserRouter() {
        UserBusinessLogic logic = new UserBusinessLogic();
        // TODO:: Content-Type에 따른 자동 ArgumentResolver 호출하는 로직이면 더 좋을 듯
        addRoute(RequestMethod.POST, "/user/create", logic::createUser, FormDataResolver.getInstance());
        addRoute(RequestMethod.POST, "/user/login", logic::login, FormDataResolver.getInstance());
        addRoute(RequestMethod.GET, "/user/logout", logic::logout, FormDataResolver.getInstance());
    }

    private <T> void addRoute(RequestMethod method, String path, BusinessHandler businessHandler, ArgumentResolver<T> resolver) {
        BusinessHandler wrappedBusinessHandler = (req, res) -> {
            String bodyText = req.body().getBodyText();
            if (bodyText != null && !bodyText.isEmpty()) {
                T parsedData = resolver.resolve(bodyText);
                req.body().setParsedBody(parsedData);
            }

            return businessHandler.execute(req, res);
        };

        businessMap.put(method.name() + " " + path, wrappedBusinessHandler);
    }

    @Override
    public BusinessHandler getHandler(HttpRequest req) {
        String key = req.line().getMethod().name() + " " + req.line().getPathUrl();

        if (businessMap.containsKey(key)) {
            return businessMap.get(key);
        }

        return null;
    }
}
