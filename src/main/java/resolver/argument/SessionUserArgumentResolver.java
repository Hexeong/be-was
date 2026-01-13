package resolver.argument;

import annotation.SessionUser;
import db.SessionStorage;
import model.http.HttpRequest;
import util.extractor.CookieExtractor;

import java.lang.reflect.Parameter;

public class SessionUserArgumentResolver implements ArgumentResolver {
    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(SessionUser.class);
    }

    @Override
    public Object resolve(Parameter parameter, HttpRequest request) {
        String sid = CookieExtractor.getValue(request, "sid");
        return SessionStorage.findUserBySid(sid);
    }
}
