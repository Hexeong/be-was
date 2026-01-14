package model;

import util.IdGenerator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Comment {
    private String commentId = IdGenerator.create();
    private String content;
    private String writerId;
    private String writerName;
    private String writerProfileUrl; // [추가] 작성자 프로필 이미지
    private String articleId;
    private String createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    public Comment() {
    }

    public Comment(String content, String writerId, String writerName, String writerProfileUrl, String articleId) {
        this.content = content;
        this.writerId = writerId;
        this.writerName = writerName;
        this.writerProfileUrl = writerProfileUrl;
        this.articleId = articleId;
    }

    public Comment(User user, String content, String articleId) {
        this.content = content;
        this.writerId = user.getUserId();
        this.writerName = user.getName();
        this.writerProfileUrl = user.getProfileImageUrl();
        this.articleId = articleId;
    }

    public Comment(String commentId, String content, String writerId, String writerName, String writerProfileUrl, String articleId, String createdAt) {
        this.commentId = commentId;
        this.content = content;
        this.writerId = writerId;
        this.writerName = writerName;
        this.writerProfileUrl = writerProfileUrl;
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

    // [추가] Getter
    public String getWriterProfileUrl() {
        return writerProfileUrl;
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

    // [추가] Setter
    public void setWriterProfileUrl(String writerProfileUrl) {
        this.writerProfileUrl = writerProfileUrl;
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
                ", writerProfileUrl='" + writerProfileUrl + '\'' + // [추가]
                ", articleId='" + articleId + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}