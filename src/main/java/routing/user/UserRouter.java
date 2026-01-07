package routing.user;

import business.Business;
import business.UserBusinessLogic;
import model.http.TotalHttpMessage;
import model.http.sub.RequestMethod;
import resolver.ArgumentResolver;
import resolver.FormDataResolver;
import routing.DomainRouter;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class UserRouter implements DomainRouter {
    private final Map<String, Business> businessMap = new HashMap<>();

    public UserRouter() {
        UserBusinessLogic logic = new UserBusinessLogic();
        // TODO:: Content-Type에 따른 자동 ArgumentResolver 호출하는 로직이면 더 좋을 듯
        addRoute(RequestMethod.POST, "/user/create", logic::createUser, FormDataResolver.getInstance());
        addRoute(RequestMethod.POST, "/user/login", logic::login, FormDataResolver.getInstance());
    }

    // [AI를 활용한 어댑터 패턴 처리]
    private <T> void addRoute(RequestMethod method, String path, Business business, ArgumentResolver<T> resolver) {
        Business wrappedBusiness = (out, message) -> {
            String bodyText = message.body().getBodyText();
            if (bodyText != null && !bodyText.isEmpty()) {
                T parsedData = resolver.resolve(bodyText);
                message.body().setParsedBody(parsedData);
            }

            business.execute(out, message);
        };

        businessMap.put(method.name() + " " + path, wrappedBusiness);
    }

    @Override
    public boolean route(OutputStream out, TotalHttpMessage message) {
        String key = message.line().getMethod().name() + " " + message.line().getPathUrl();

        if (businessMap.containsKey(key)) {
            businessMap.get(key).execute(out, message);
            return true;
        }

        return false;
    }
}
