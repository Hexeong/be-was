package model;

import util.IdGenerator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Comment {
    private String commentId = IdGenerator.create();
    private String content;
    private String writerId;
    private String writerName;
    private String articleId;
    private String createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    public Comment() {
    }

    // 서비스 로직용 생성자
    public Comment(String content, String writerId, String writerName, String articleId) {
        this.content = content;
        this.writerId = writerId;
        this.writerName = writerName;
        this.articleId = articleId;
    }

    // DB 조회용 생성자
    public Comment(String commentId, String content, String writerId, String writerName, String articleId, String createdAt) {
        this.commentId = commentId;
        this.content = content;
        this.writerId = writerId;
        this.writerName = writerName;
        this.articleId = articleId;
        this.createdAt = createdAt;
    }

    public String getCommentId() {
        return commentId;
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

    public String getArticleId() {
        return articleId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setWriterId(String writerId) {
        this.writerId = writerId;
    }

    public void setWriterName(String writerName) {
        this.writerName = writerName;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentId='" + commentId + '\'' +
                ", content='" + content + '\'' +
                ", writerId='" + writerId + '\'' +
                ", writerName='" + writerName + '\'' +
                ", articleId='" + articleId + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}