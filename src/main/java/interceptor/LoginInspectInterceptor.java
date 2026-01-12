package interceptor;

import annotation.LoginRequired;
import db.SessionStorage;
import exception.CustomException;
import exception.ErrorCode;
import extractor.http.CookieExtractor;
import handler.HandlerMethod;
import model.http.HttpRequest;
import model.http.HttpResponse;

public class LoginInspectInterceptor implements Interceptor {

    private static final String COOKIE_SESSION_ID = "sid";

    @Override
    public boolean preHandle(HttpRequest req, HttpResponse res, Object handler) throws CustomException {
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            if (hm.getHandlerMethod().isAnnotationPresent(LoginRequired.class)) {
                if (!isLoginStatus(req)) {
                    throw new CustomException(ErrorCode.NOT_AUTHORIZED_ACCESS);
                }
            }
        }
        return true;
    }

    private boolean isLoginStatus(HttpRequest req) {
        String sid = CookieExtractor.getValue(req, COOKIE_SESSION_ID);
        return SessionStorage.findUserBySid(sid) != null;
    }
}
