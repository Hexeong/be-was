package handler.impl;

import annotation.Router;
import annotation.RequestMapping;
import db.Database;
import db.SessionStorage;
import extractor.http.CookieExtractor;
import model.Model;
import model.http.HttpRequest;
import model.http.HttpResponse;
import model.http.sub.RequestMethod;
import model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resolver.view.ModelAndView;

import java.util.concurrent.ThreadLocalRandom;

@Router
public class UserHttpHandler implements DynamicHttpHandler {
    private static final Logger log = LoggerFactory.getLogger(UserHttpHandler.class);

    private static final String COOKIE_HEADER_KEY = "Set-Cookie";
    private static final String COOKIE_HEADER_PREFIX = "sid=";
    private static final String SESSION_ID_KEY = "sid";
    private static final String COOKIE_HEADER_SUFFIX = "; Path=/";

    public UserHttpHandler() {}

    @RequestMapping(method = RequestMethod.POST, path = "/user/create")
    public ModelAndView createUser(HttpResponse res, User user) {

        // TODO:: 방어로직 필요

        Database.addUser(user);

        setSessionCookie(res, user);

        return new ModelAndView("redirect:/");
    }

    @RequestMapping(method = RequestMethod.POST, path = "/user/login")
    public ModelAndView login(HttpResponse res, User user) {

        // TODO:: 방어로직 필요

        User findUser = Database.findUserById(user.getUserId());
        if (findUser != null && findUser.getPassword().equals(user.getPassword())) {
            setSessionCookie(res, findUser);
            return new ModelAndView(null, "redirect:/");
        }
        return new ModelAndView( "redirect:/login");
    }

    @RequestMapping(method = RequestMethod.GET, path = "/user/logout")
    public ModelAndView logout(HttpRequest req, HttpResponse res) {
        expireSessionCookie(res);
        SessionStorage.removeSession(CookieExtractor.getValue(req, SESSION_ID_KEY));
        return new ModelAndView( "redirect:/");
    }

    private void setSessionCookie(HttpResponse res, User user) {
        int randomSid = ThreadLocalRandom.current().nextInt(100000, 1000000);
        SessionStorage.addSession(String.valueOf(randomSid), user);
        res.headers().put(COOKIE_HEADER_KEY, COOKIE_HEADER_PREFIX + randomSid + COOKIE_HEADER_SUFFIX);
    }

    private void expireSessionCookie(HttpResponse res) {
        // sid 값을 비우고, Max-Age=0으로 설정하여 브라우저가 즉시 삭제하게 함
        // Set-Cookie 값을 응답으로 보낼 시 다른 쿠키는 영향 받지 않기에 괜찮음
        res.headers().put(COOKIE_HEADER_KEY, "sid=; Path=/; Max-Age=0");
    }
}
