package handler.impl;

import annotation.Router;
import annotation.RequestMapping;
import annotation.Transactional;
import dao.UserDao;
import db.SessionStorage;
import exception.CustomException;
import exception.ErrorCode;
import model.Model;
import model.http.HttpStatus;
import util.extractor.CookieExtractor;
import model.http.HttpRequest;
import model.http.HttpResponse;
import model.http.sub.RequestMethod;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resolver.view.ModelAndView;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Router
public class UserHttpHandler implements DynamicHttpHandler {
    private static final Logger log = LoggerFactory.getLogger(UserHttpHandler.class);

    private static final String COOKIE_HEADER_KEY = "Set-Cookie";
    private static final String HEADER_LOCATION = "Location";
    private static final String SESSION_ID_KEY = "sid";
    private static final String COOKIE_HEADER_SUFFIX = "; Path=/";

    public UserHttpHandler() {}

    @Transactional
    @RequestMapping(method = RequestMethod.POST, path = "/user/create")
    public ModelAndView createUser(HttpResponse res, User user) {

        // TODO:: 방어로직 필요
        // TODO:: 각 필드에 대한 4글자 최소 길이 검사 로직 필요

        UserDao.create(user);
        setSessionCookie(res, user);

        return new ModelAndView("redirect:/");
    }

    @RequestMapping(method = RequestMethod.POST, path = "/user/login")
    public ModelAndView login(HttpResponse res, Model model, User user) {

        Optional<User> findUser = UserDao.findById(user.getUserId());
        if (findUser.isEmpty()) {
            String encodedMsg = URLEncoder.encode("존재하지 않는 아이디입니다. 회원 가입 하시겠습니까?", StandardCharsets.UTF_8);
            String encodedUrl = URLEncoder.encode("/registration", StandardCharsets.UTF_8);
            res.addHeader(COOKIE_HEADER_KEY, "confirmMessage=" + encodedMsg + "; Path=/");
            res.addHeader(COOKIE_HEADER_KEY, "confirmUrl=" + encodedUrl + "; Path=/");

            return new ModelAndView(model, "redirect:/login");
        }

        if (!findUser.get().getPassword().equals(user.getPassword())) {
            String encodedMsg = URLEncoder.encode("비밀번호가 틀렸습니다.", StandardCharsets.UTF_8);
            res.addHeader(COOKIE_HEADER_KEY, "alertMessage=" + encodedMsg + "; Path=/");

            return new ModelAndView(model, "redirect:/login");
        }

        setSessionCookie(res, findUser.get());
        return new ModelAndView(model, "redirect:/");
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
        res.addHeader(COOKIE_HEADER_KEY, SESSION_ID_KEY + "=" + randomSid + COOKIE_HEADER_SUFFIX);
    }

    private void expireSessionCookie(HttpResponse res) {
        // sid 값을 비우고, Max-Age=0으로 설정하여 브라우저가 즉시 삭제하게 함
        // Set-Cookie 값을 응답으로 보낼 시 다른 쿠키는 영향 받지 않기에 괜찮음
        res.addHeader(COOKIE_HEADER_KEY, "sid=; Path=/; Max-Age=0");
    }
}
