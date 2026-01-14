package handler.impl;

import annotation.*;
import dao.ArticleDao;
import exception.CustomException;
import exception.ErrorCode;
import model.Article;
import model.Model;
import model.User;
import model.http.HttpRequest;
import model.http.HttpResponse;
import model.http.HttpStatus;
import model.http.sub.RequestMethod;
import model.request.ArticleCreateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resolver.view.ModelAndView;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Router
public class ArticleHttpHandler implements DynamicHttpHandler {

    private static final Logger log = LoggerFactory.getLogger(ArticleHttpHandler.class);

    private static final String COOKIE_HEADER_KEY = "Set-Cookie";

    @LoginRequired
    @RequestMapping(method = RequestMethod.POST, path = "/article")
    public ModelAndView registerArticle(HttpResponse res, Model model, @SessionUser User user,
                                        @MultipartFormdata ArticleCreateRequest articleReq) {

        Article article = new Article(user, articleReq.getContent());

        if (articleReq.getFile() == null) {
            String encodedMsg = URLEncoder.encode("게시물은 파일 업로드하지 않고는 생성할 수 없습니다.", StandardCharsets.UTF_8);
            res.addHeader(COOKIE_HEADER_KEY, "alertMessage=" + encodedMsg + "; Path=/");

            return new ModelAndView(model, "redirect:/article");
        }

        articleReq.getFile().saveFileAs(article.getArticleId());
        String extension = articleReq.getFile().getExtension();
        String imageUrl = "/uploads/" + article.getArticleId() + extension;
        article.setImageUrl(imageUrl);

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

        return new ModelAndView("/index.html");
    }
}