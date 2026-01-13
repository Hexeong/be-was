package handler.impl;

import annotation.*;
import dao.ArticleDao;
import exception.CustomException;
import exception.ErrorCode;
import model.Article;
import model.User;
import model.http.HttpRequest;
import model.http.HttpResponse;
import model.http.HttpStatus;
import model.http.sub.RequestMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resolver.view.ModelAndView;

@Router
public class ArticleHttpHandler implements DynamicHttpHandler {

    private static final Logger log = LoggerFactory.getLogger(ArticleHttpHandler.class);

    /**
     * 로그인 인증 처리가 필요한 HandlerMethod입니다.
     * 해당 HandlerMethod는 POST /article 으로 HTTP 요청이 올 경우, 사용자가 formdata로 보낸 데이터로
     * DB에 새로운 게시물(Article)을 생성합니다. 그 이후 생성한 게시물 페이지로 이동합니다.
     * @param user
     * @param article
     * @return
     */
    @LoginRequired
    @RequestMapping(method = RequestMethod.POST, path = "/article")
    public ModelAndView registerArticle(@SessionUser User user, Article article) {
        setWriterInfo(user, article);
        ArticleDao.create(article);

        int page = ArticleDao.findIndexByArticleId(article.getArticleId());

        return new ModelAndView("redirect:/?page=" + page);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/article/like")
    public ModelAndView increaseLikeCnt(HttpRequest req, HttpResponse res) {
        Object articleId = req.line().getQueryParameterList().get("articleId");
        if (articleId == null)
            throw new CustomException(ErrorCode.BAD_REQUEST);

        ArticleDao.increaseLikeCnt(articleId.toString());
        res.setStatus(HttpStatus.OK);

        return new ModelAndView("/index.html"); // 버리는 값
    }

    private void setWriterInfo(User user, Article article) {
        article.setWriterId(user.getUserId());
        article.setWriterName(user.getName());
    }
}
