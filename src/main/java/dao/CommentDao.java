package dao;

import db.TransactionManager;
import model.Comment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommentDao {

    public static void create(Comment comment) {
        String sql = "INSERT INTO COMMENT (commentId, content, writerId, writerName, articleId, createdAt) VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = TransactionManager.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, comment.getCommentId());
            pstmt.setString(2, comment.getContent());
            pstmt.setString(3, comment.getWriterId());
            pstmt.setString(4, comment.getWriterName());
            pstmt.setString(5, comment.getArticleId());
            pstmt.setString(6, comment.getCreatedAt());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("댓글 작성 실패", e);
        } finally {
            close(pstmt);
            TransactionManager.closeConnection(conn);
        }
    }

    public static List<Comment> findAllByArticleId(String articleId) {
        String sql = "SELECT c.*, u.profileImageUrl " +
                "FROM COMMENT c " +
                "LEFT JOIN USERS u ON c.writerId = u.userId " +
                "WHERE c.articleId = ? " +
                "ORDER BY c.createdAt ASC";

        List<Comment> comments = new ArrayList<>();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = TransactionManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, articleId);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                // [수정] Comment 생성자에 profileImageUrl 추가 (u.profileImageUrl)
                comments.add(new Comment(
                        rs.getString("commentId"),
                        rs.getString("content"),
                        rs.getString("writerId"),
                        rs.getString("writerName"),
                        rs.getString("profileImageUrl"), // JOIN으로 가져온 컬럼
                        rs.getString("articleId"),
                        rs.getString("createdAt")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException("댓글 목록 조회 실패", e);
        } finally {
            close(rs);
            close(pstmt);
            TransactionManager.closeConnection(conn);
        }
        return comments;
    }

    private static void close(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}