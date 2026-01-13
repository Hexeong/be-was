package dao;

import db.TransactionManager;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserDao {

    public static void create(User user) {
        String sql = "INSERT INTO USERS (userId, password, name, email) VALUES (?, ?, ?, ?)";

        Connection conn = null; // 수동으로 트랜잭션 제어를 위함
        PreparedStatement pstmt = null;  // pstmt는 수동으로 생성

        try {
            conn = TransactionManager.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getName());
            pstmt.setString(4, user.getEmail());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("사용자 생성 실패", e);
        } finally {
            // [중요] 자원 해제 순서 준수 (Statement는 무조건 닫고, Connection은 매니저에게 위임)
            close(pstmt);
            TransactionManager.closeConnection(conn);
        }
    }

    public static Optional<User> findById(String userId) {
        String sql = "SELECT userId, password, name, email FROM USERS WHERE userId = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = TransactionManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new User(
                        rs.getString("userId"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("email")
                ));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("사용자 조회 실패", e);
        } finally {
            close(rs);
            close(pstmt);
            TransactionManager.closeConnection(conn);
        }
    }

    public static void editInfo(User user) {
        String sql = "UPDATE USERS SET password = ?, name = ?, email = ? WHERE userId = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = TransactionManager.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, user.getPassword());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getUserId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("사용자 정보 수정 실패", e);
        } finally {
            close(pstmt);
            TransactionManager.closeConnection(conn);
        }
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