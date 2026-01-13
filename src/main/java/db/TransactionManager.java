package db;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionManager {

    private static final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();

    // 1. 트랜잭션 시작
    public static void startTransaction() throws SQLException {
        Connection conn = JdbcConnection.getConnection();
        conn.setAutoCommit(false); // 수동 커밋 모드
        connectionHolder.set(conn);
    }

    // 2. 커밋 (트랜잭션 종료 1)
    public static void commit() throws SQLException {
        Connection conn = connectionHolder.get();
        if (conn != null) {
            try {
                conn.commit();
            } finally {
                closeAndRemoveConnection(conn);
            }
        }
    }

    // 3. 롤백 (트랜잭션 종료 2)
    public static void rollback() {
        Connection conn = connectionHolder.get();
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                closeAndRemoveConnection(conn);
            }
        }
    }

    // 4. 진짜 연결 종료 및 ThreadLocal 제거
    // commit이나 rollback이 호출될 때만 실행됩니다.
    private static void closeAndRemoveConnection(Connection conn) {
        try {
            conn.setAutoCommit(true); // 커넥션 풀 반환 대비(선택사항)
            conn.close();             // 물리적 연결 종료
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connectionHolder.remove(); // ThreadLocal 초기화
        }
    }

    // 5. [DAO용] 안전한 닫기 메서드
    // DAO는 이 메서드를 호출합니다. 트랜잭션 중이면 닫지 않고, 아니면 닫습니다.
    public static void closeConnection(Connection conn) {
        try {
            Connection txConn = connectionHolder.get();

            // 트랜잭션 진행 중이고, 그 커넥션이라면 -> 닫지 않고 리턴 (유지)
            if (conn != null && conn == txConn) {
                return;
            }

            // 트랜잭션이 아닐 때만 진짜 종료
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 6. 커넥션 획득
    public static Connection getConnection() throws SQLException {
        Connection conn = connectionHolder.get();
        if (conn != null) {
            return conn; // 트랜잭션 중이면 재사용
        }
        return JdbcConnection.getConnection(); // 아니면 새거 생성
    }
}