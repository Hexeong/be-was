package model;

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
    private String writerProfileUrl;
    private String createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    public Article() {
    }

    public Article(User user, String content) {
        this.content = content;
        this.writerId = user.getUserId();
        this.writerName = user.getName();
        this.writerProfileUrl = user.getProfileImageUrl();
    }

    // 서비스 로직용 생성자 (테스트 등 용도)
    public Article(String content, String imageUrl, int likeCnt, String writerId, String writerName, String writerProfileUrl) {
        this.content = content;
        this.imageUrl = imageUrl;
        this.likeCnt = likeCnt;
        this.writerId = writerId;
        this.writerName = writerName;
        this.writerProfileUrl = writerProfileUrl;
    }

    public Article(String articleId, String content, String imageUrl, int likeCnt, String writerId, String writerName, String writerProfileUrl, String createdAt) {
        this.articleId = articleId;
        this.content = content;
        this.imageUrl = imageUrl;
        this.likeCnt = likeCnt;
        this.writerId = writerId;
        this.writerName = writerName;
        this.writerProfileUrl = writerProfileUrl;
        this.createdAt = createdAt;
    }

    public String getArticleId() {
        return articleId;
    }

    public String getContent() {
        return content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getWriterId() {
        return writerId;
    }

    public String getWriterName() {
        return writerName;
    }

    public String getWriterProfileUrl() {
        return writerProfileUrl;
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

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setWriterId(String writerId) {
        this.writerId = writerId;
    }

    public void setWriterName(String writerName) {
        this.writerName = writerName;
    }

    public void setWriterProfileUrl(String writerProfileUrl) {
        this.writerProfileUrl = writerProfileUrl;
    }

    @Override
    public String toString() {
        return "Article{" +
                "articleId='" + articleId + '\'' +
                ", content='" + content + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", likeCnt=" + likeCnt +
                ", writerId='" + writerId + '\'' +
                ", writerName='" + writerName + '\'' +
                ", writerProfileUrl='" + writerProfileUrl + '\'' + // [추가]
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}