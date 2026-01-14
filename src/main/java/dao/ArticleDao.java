package dao;

import db.TransactionManager;
import model.Article;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArticleDao {

    public static void create(Article article) {
        String sql = "INSERT INTO ARTICLE (articleId, content, imageUrl, likeCnt, writerId, writerName, createdAt) VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = TransactionManager.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, article.getArticleId());
            pstmt.setString(2, article.getContent());
            pstmt.setString(3, article.getImageUrl());
            pstmt.setInt(4, article.getLikeCnt());
            pstmt.setString(5, article.getWriterId());
            pstmt.setString(6, article.getWriterName());
            pstmt.setString(7, article.getCreatedAt());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("게시글 작성 실패", e);
        } finally {
            close(pstmt);
            TransactionManager.closeConnection(conn);
        }
    }

    public static List<Article> findAll() {
        String sql = "SELECT * FROM ARTICLE ORDER BY createdAt DESC";
        List<Article> articles = new ArrayList<>();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = TransactionManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                articles.add(mapResultSetToArticle(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("게시글 목록 조회 실패", e);
        } finally {
            close(rs);
            close(pstmt);
            TransactionManager.closeConnection(conn);
        }
        return articles;
    }

    public static Optional<Article> findByIndex(int index) {
        String sql = "SELECT * FROM ARTICLE ORDER BY createdAt DESC LIMIT 1 OFFSET ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = TransactionManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, index);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToArticle(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("게시글 조회 실패 (Index: " + index + ")", e);
        } finally {
            close(rs);
            close(pstmt);
            TransactionManager.closeConnection(conn);
        }
    }

    public static int count() {
        String sql = "SELECT count(*) FROM ARTICLE";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = TransactionManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;

        } catch (SQLException e) {
            throw new RuntimeException("게시글 개수 조회 실패", e);
        } finally {
            close(rs);
            close(pstmt);
            TransactionManager.closeConnection(conn);
        }
    }

    public static int findIndexByArticleId(String articleId) {
        String sql = "SELECT rnum FROM (" +
                "    SELECT articleId, ROW_NUMBER() OVER (ORDER BY createdAt DESC) as rnum " +
                "    FROM ARTICLE" +
                ") t WHERE articleId = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = TransactionManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, articleId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) - 1;
            }

            return -1;

        } catch (SQLException e) {
            throw new RuntimeException("게시글 인덱스 조회 실패", e);
        } finally {
            close(rs);
            close(pstmt);
            TransactionManager.closeConnection(conn);
        }
    }

    public static void increaseLikeCnt(String articleId) {
        String sql = "UPDATE ARTICLE SET likeCnt = likeCnt + 1 WHERE articleId = ?";
        executeUpdate(sql, articleId);
    }

    private static void executeUpdate(String sql, String articleId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = TransactionManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, articleId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("게시글 업데이트 실패", e);
        } finally {
            close(pstmt);
            TransactionManager.closeConnection(conn);
        }
    }

    private static Article mapResultSetToArticle(ResultSet rs) throws SQLException {
        return new Article(
                rs.getString("articleId"),
                rs.getString("content"),
                rs.getString("imageUrl"),
                rs.getInt("likeCnt"),
                rs.getString("writerId"),
                rs.getString("writerName"),
                rs.getString("createdAt")
        );
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