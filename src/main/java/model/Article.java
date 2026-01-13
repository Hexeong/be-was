package model;

import util.IdGenerator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Article {
    private String articleId = IdGenerator.create();
    private String content;
    private int likeCnt;
    private String writerId;
    private String writerName;
    private String createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    public Article() {
    }

    // 서비스 로직용 생성자
    public Article(String content, int likeCnt, String writerId, String writerName) {
        this.content = content;
        this.likeCnt = likeCnt;
        this.writerId = writerId;
        this.writerName = writerName;
    }

    // DB 조회용 생성자
    public Article(String articleId, String content, int likeCnt, String writerId, String writerName, String createdAt) {
        this.articleId = articleId;
        this.content = content;
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
}