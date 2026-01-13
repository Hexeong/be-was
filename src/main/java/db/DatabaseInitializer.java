package db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    private static final String INIT_SQL_PATH = "static/init.sql";

    public static void init() {
        String sql = loadSqlFile();

        try (Connection conn = JdbcConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // H2 Database는 여러 쿼리가 세미콜론(;)으로 연결되어 있어도 stmt.execute()로 한 번에 실행 가능합니다.
            stmt.execute(sql);
            log.info("Database initialized successfully using {}", INIT_SQL_PATH);

        } catch (SQLException e) {
            log.error("Database initialization failed", e);
            throw new RuntimeException(e);
        }
    }

    private static String loadSqlFile() {
        try (InputStream inputStream = DatabaseInitializer.class.getClassLoader().getResourceAsStream(INIT_SQL_PATH)) {

            if (inputStream == null) {
                throw new RuntimeException("Cannot find SQL file: " + INIT_SQL_PATH);
            }

            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new RuntimeException("Failed to read SQL file: " + INIT_SQL_PATH, e);
        }
    }
}