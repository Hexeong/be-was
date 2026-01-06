package routing;

import business.BusinessHandler;
import business.IndexBusinessLogic;
import model.http.HttpRequest;
import model.http.sub.RequestMethod;
import resolver.argument.ArgumentResolver;
import resolver.argument.FormDataResolver;

import java.util.HashMap;
import java.util.Map;

public class DefaultRouter implements DomainRouter {
    private final Map<String, BusinessHandler> businessMap = new HashMap<>();

    public DefaultRouter() {
        IndexBusinessLogic logic = new IndexBusinessLogic();
        // TODO:: Content-Type에 따른 자동 ArgumentResolver 호출하는 로직이면 더 좋을 듯
        addRoute(RequestMethod.GET, "/", logic::indexPage, FormDataResolver.getInstance());
        addRoute(RequestMethod.GET, "/registration", logic::registrationPage, FormDataResolver.getInstance());
        addRoute(RequestMethod.GET, "/login", logic::loginPage, FormDataResolver.getInstance());

    }

    // [AI를 활용한 어댑터 패턴 처리]
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
