package model.request;

import model.MultipartFile;

public class ArticleCreateRequest {
    private String content;
    private String articleId;
    private MultipartFile file;

    public ArticleCreateRequest() {}

    public String getContent() {
        return content;
    }

    public String getArticleId() {
        return articleId;
    }

    public MultipartFile getFile() {
        return file;
    }
}