package handler.impl;

import annotation.*;
import dao.ArticleDao;
import dao.CommentDao;
import db.SessionStorage;
import exception.CustomException;
import exception.ErrorCode;
import model.Article;
import model.Comment;
import util.extractor.CookieExtractor;
import model.Model;
import model.http.HttpRequest;
import model.http.sub.RequestMethod;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resolver.view.ModelAndView;

import java.util.List;
import java.util.Optional;

@Router
public class DynamicPageHttpHandler implements DynamicHttpHandler {
    private static final Logger log = LoggerFactory.getLogger(DynamicPageHttpHandler.class);

    private static final String SESSION_ID_KEY = "sid";

    public DynamicPageHttpHandler() {}

    @Transactional
    @RequestMapping(method = RequestMethod.GET, path = {"/", "/index.html"})
    public ModelAndView indexPage(HttpRequest req, Model model) {
        String page = req.line().getQueryParameterList().getOrDefault("page", "0").toString();
        int pageNum = Integer.parseInt(page);
        int totalCount = ArticleDao.count();

        Optional<Article> findArticle = ArticleDao.findByIndex(pageNum);
        if (findArticle.isEmpty() && totalCount > 0) {
            pageNum = 0;
            findArticle = ArticleDao.findByIndex(0);
        }

        Article article = findArticle.orElseThrow(() -> new CustomException(ErrorCode.NO_ARTICLE_DATA));
        model.put("article", article);

        List<Comment> comments = CommentDao.findAllByArticleId(article.getArticleId());
        model.put("comments", comments);

        if (pageNum > 0) { // 이전 글, 현재 페이지가 0보다 커야 존재함
            model.put("hasPrev", true);
            model.put("prevId", pageNum - 1);
        } else {
            model.put("noPrev", true);
        }

        if (pageNum < totalCount - 1) { // 다음 글, 현재 페이지가 (전체 개수 - 1)보다 작아야 존재함
            model.put("hasNext", true);
            model.put("nextId", pageNum + 1);
        } else {
            model.put("noNext", true);
        }

        if (isLoginStatus(req, model)) {
            return new ModelAndView(model, "/main/index.html");
        }
        return new ModelAndView(model, "/index.html");
    }

    @RequestMapping(method = RequestMethod.GET, path = {"/registration", "/registration/index.html"})
    public ModelAndView registrationPage(HttpRequest req, Model model) {
        return new ModelAndView(model, "/registration/index.html");
    }

    @RequestMapping(method = RequestMethod.GET, path = {"/login", "/login/index.html"})
    public ModelAndView loginPage(HttpRequest req, Model model) {
        return new ModelAndView(model, "/login/index.html");
    }

    @LoginRequired
    @RequestMapping(method = RequestMethod.GET, path = {"/mypage", "/mypage/index.html"})
    public ModelAndView myPage(@SessionUser User user, Model model) {
        model.put("username", user.getName());
        return new ModelAndView(model, "/mypage/index.html");
    }

    @LoginRequired
    @RequestMapping(method = RequestMethod.GET, path = {"/article", "/article/index.html"})
    public ModelAndView article(@SessionUser User user, Model model) {
        model.put("username", user.getName());
        return new ModelAndView(model, "/article/index.html");
    }

    @LoginRequired
    @RequestMapping(method = RequestMethod.GET, path = {"/comment", "/comment/index.html"})
    public ModelAndView comment(HttpRequest req, @SessionUser User user, Model model) {
        model.put("username", user.getName());

        Object articleId = req.line().getQueryParameterList().get("articleId");
        if (articleId == null)
            throw new CustomException(ErrorCode.BAD_REQUEST);
        model.put("articleId", articleId.toString());

        return new ModelAndView(model, "/comment/index.html");
    }

    private boolean isLoginStatus(HttpRequest req, Model model) {
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

        return isLoggedIn;
    }
}
