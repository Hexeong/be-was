package handler.impl;

import annotation.LoginRequired;
import annotation.Router;
import annotation.RequestMapping;
import db.SessionStorage;
import util.extractor.CookieExtractor;
import model.Model;
import model.http.HttpRequest;
import model.http.sub.RequestMethod;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resolver.view.ModelAndView;

@Router
public class DynamicPageHttpHandler implements DynamicHttpHandler {
    private static final Logger log = LoggerFactory.getLogger(DynamicPageHttpHandler.class);

    private static final String SESSION_ID_KEY = "sid";

    public DynamicPageHttpHandler() {}

    @RequestMapping(method = RequestMethod.GET, path = {"/", "/index.html"})
    public ModelAndView indexPage(HttpRequest req, Model model) {
        setLoginStatus(req, model);
        return new ModelAndView(model, "/index.html");
    }

    @RequestMapping(method = RequestMethod.GET, path = {"/registration", "/registration/index.html"})
    public ModelAndView registrationPage(HttpRequest req, Model model) {
        setLoginStatus(req, model);
        return new ModelAndView(model, "/registration/index.html");
    }

    @RequestMapping(method = RequestMethod.GET, path = {"/login", "/login/index.html"})
    public ModelAndView loginPage(HttpRequest req, Model model) {
        setLoginStatus(req, model);
        return new ModelAndView(model, "/login/index.html");
    }

    @LoginRequired
    @RequestMapping(method = RequestMethod.GET, path = {"/mypage", "/mypage/index.html"})
    public ModelAndView myPage(HttpRequest req, Model model) {
        setLoginStatus(req, model);
        return new ModelAndView(model, "/mypage/index.html");
    }

    @LoginRequired
    @RequestMapping(method = RequestMethod.GET, path = {"/article", "/article/index.html"})
    public ModelAndView article(HttpRequest req) {
        return new ModelAndView("/article/index.html");
    }

    private void setLoginStatus(HttpRequest req, Model model) {
        boolean isLoggedIn = false;

        String sid = CookieExtractor.getValue(req, SESSION_ID_KEY);

        if (sid != null) {
            User user = SessionStorage.findUserBySid(sid);
            if (user != null) {
                log.debug("로그인 인증 성공 - User: {}, SID: {}", user.getName(), sid);
                isLoggedIn = true;
                model.put("username", user.getName());
            } else {
                log.debug("유효하지 않은 세션 ID (만료되었거나 조작됨) - SID: {}", sid);
            }
        }

        if (!isLoggedIn) {
            log.debug("비로그인 상태 또는 쿠키 없음");
        }

        // 4. 모델 설정
        model.put("isLoggedIn", isLoggedIn);
        model.put("isNotLoggedIn", !isLoggedIn);
    }
}