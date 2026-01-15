package interceptor;

import annotation.LoginRequired;
import db.SessionStorage;
import exception.CustomException;
import exception.ErrorCode;
import model.http.HttpStatus;
import util.extractor.CookieExtractor;
import handler.HandlerMethod;
import model.http.HttpRequest;
import model.http.HttpResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class LoginInspectInterceptor implements Interceptor {

    private static final String COOKIE_HEADER_KEY = "Set-Cookie";
    private static final String COOKIE_SESSION_ID = "sid";
    private static final String HEADER_LOCATION = "Location";

    @Override
    public boolean preHandle(HttpRequest req, HttpResponse res, Object handler) throws CustomException {
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            if (hm.getHandlerMethod().isAnnotationPresent(LoginRequired.class)) {
                LoginRequired loginRequired = hm.getHandlerMethod().getAnnotation(LoginRequired.class);
                if (!isLoginStatus(req)) {
                    String redirectPathOnFail = loginRequired.redirectPathOnFail();
                    res.setStatus(HttpStatus.FOUND);
                    res.addHeader(HEADER_LOCATION, redirectPathOnFail);

                    String encodedMsg = URLEncoder.encode("인증이 필요한 서비스입니다.", StandardCharsets.UTF_8);
                    res.addHeader(COOKIE_HEADER_KEY, "alertMessage=" + encodedMsg + "; Path=/");
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
