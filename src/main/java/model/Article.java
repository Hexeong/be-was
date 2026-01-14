package model;

import model.request.ArticleCreateRequest;
import util.IdGenerator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Article {
    private String articleId = IdGenerator.create();
    private String content;
    private String imageUrl;
    private int likeCnt = 0;
    private String writerId;
    private String writerName;
    private String createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    public Article() {
    }

    public Article(User user, String content) {
        this.content = content;
        this.writerId = user.getUserId();
        this.writerName = user.getName();
    }

    // 서비스 로직용 생성자 (이미지 포함)
    public Article(String content, String imageUrl, int likeCnt, String writerId, String writerName) {
        this.content = content;
        this.imageUrl = imageUrl; // [추가]
        this.likeCnt = likeCnt;
        this.writerId = writerId;
        this.writerName = writerName;
    }

    // DB 조회용 생성자 (이미지 포함)
    public Article(String articleId, String content, String imageUrl, int likeCnt, String writerId, String writerName, String createdAt) {
        this.articleId = articleId;
        this.content = content;
        this.imageUrl = imageUrl; // [추가]
        this.likeCnt = likeCnt;
        this.writerId = writerId;
        this.writerName = writerName;
        this.createdAt = createdAt;
    }

    public String getArticleId() {
        return articleId;
    }

    public String getContent() {
        return content;
    }

    // [추가] Getter
    public String getImageUrl() {
        return imageUrl;
    }

    public String getWriterId() {
        return writerId;
    }

    public String getWriterName() {
        return writerName;
    }

    public int getLikeCnt() {
        return likeCnt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setLikeCnt(int likeCnt) {
        this.likeCnt = likeCnt;
    }

    // [추가] Setter (필요시)
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setWriterId(String writerId) {
        this.writerId = writerId;
    }

    public void setWriterName(String writerName) {
        this.writerName = writerName;
    }

    @Override
    public String toString() {
        return "Article{" +
                "articleId='" + articleId + '\'' +
                ", content='" + content + '\'' +
                ", imageUrl='" + imageUrl + '\'' + // [추가]
                ", likeCnt=" + likeCnt +
                ", writerId='" + writerId + '\'' +
                ", writerName='" + writerName + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}