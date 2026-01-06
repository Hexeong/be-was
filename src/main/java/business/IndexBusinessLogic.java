package business;

import db.SessionStorage;
import extractor.http.CookieExtractor;
import model.Model;
import model.http.HttpRequest;
import model.http.HttpResponse;
import model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resolver.view.ModelAndView;

public class IndexBusinessLogic {
    private static final Logger log = LoggerFactory.getLogger(IndexBusinessLogic.class);

    private static final String SESSION_ID_KEY = "sid";

    public ModelAndView indexPage(HttpRequest req, HttpResponse res) {
        Model model = new Model();
        setLoginStatus(req, model);
        return new ModelAndView(model, "/index.html");
    }

    public ModelAndView registrationPage(HttpRequest req, HttpResponse res) {
        Model model = new Model();
        setLoginStatus(req, model);
        return new ModelAndView(model, "/registration/index.html");
    }

    public ModelAndView loginPage(HttpRequest req, HttpResponse res) {
        Model model = new Model();
        setLoginStatus(req, model);
        return new ModelAndView(model, "/login/index.html");
    }

    private void setLoginStatus(HttpRequest req, Model model) {
        boolean isLoggedIn = false;

        String sid = CookieExtractor.getValue(req, SESSION_ID_KEY);

        if (sid != null) {
            User user = SessionStorage.findUserBySid(sid);
            if (user != null) {
                log.debug("로그인 인증 성공 - User: {}, SID: {}", user.getName(), sid);
                isLoggedIn = true;
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