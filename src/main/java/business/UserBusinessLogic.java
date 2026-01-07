package business;

import db.Database;
import db.SessionStorage;
import extractor.http.CookieExtractor;
import model.Model;
import model.http.HttpRequest;
import model.http.HttpResponse;
import model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resolver.view.ModelAndView;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class UserBusinessLogic {
    private static final Logger log = LoggerFactory.getLogger(UserBusinessLogic.class);

    private static final String COOKIE_HEADER_KEY = "Set-Cookie";
    private static final String COOKIE_HEADER_PREFIX = "sid=";
    private static final String SESSION_ID_KEY = "sid";
    private static final String COOKIE_HEADER_SUFFIX = "; Path=/";

    public ModelAndView createUser(HttpRequest req, HttpResponse res) {
        Map<String, String> formData = req.body().getParsedBody(Map.class);

        User user = new User(
                formData.getOrDefault("userId", ""),
                formData.getOrDefault("password", ""),
                formData.getOrDefault("name", ""),
                formData.getOrDefault("email", "")
        );
        Database.addUser(user);

        log.debug(user.toString());

        setSessionCookie(res, user);

        return new ModelAndView(new Model(), "redirect:/");
    }

    public ModelAndView login(HttpRequest req, HttpResponse res) {
        Map<String, String> formData = req.body().getParsedBody(Map.class);

        User findUser = Database.findUserById(formData.getOrDefault("userId", ""));
        if (findUser != null && findUser.getPassword().equals(formData.getOrDefault("password", ""))) {
            log.debug("로그인 성공");
            setSessionCookie(res, findUser);
            return new ModelAndView(null, "redirect:/");
        }

        log.debug("로그인 실패! \n" + findUser + "\n" + req.body().getBodyText());
        return new ModelAndView(new Model(), "redirect:/login");
    }

    public ModelAndView logout(HttpRequest req, HttpResponse res) {
        expireSessionCookie(res);
        SessionStorage.removeSession(CookieExtractor.getValue(req, SESSION_ID_KEY));
        return new ModelAndView(new Model(), "redirect:/");
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
