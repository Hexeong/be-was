package handler.impl;

import annotation.*;
import dao.ArticleDao;
import dao.CommentDao;
import model.Comment;
import model.User;
import model.http.sub.RequestMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resolver.view.ModelAndView;

@Router
public class CommentHttpHandler implements DynamicHttpHandler{

    private static final Logger log = LoggerFactory.getLogger(CommentHttpHandler.class);

    @Transactional
    @LoginRequired
    @RequestMapping(method = RequestMethod.POST, path = "/comment")
    public ModelAndView registerComment(@SessionUser User user, Comment comment) {
        setWriterInfo(user, comment);
        CommentDao.create(comment);

        int page = ArticleDao.findIndexByArticleId(comment.getArticleId());
        return new ModelAndView("redirect:/?page=" + page);
    }

    private void setWriterInfo(User user, Comment comment) {
        comment.setWriterId(user.getUserId());
        comment.setWriterName(user.getName());
    }
}
