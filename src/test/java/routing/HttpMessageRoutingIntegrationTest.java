package routing;

import db.Database;
import fixture.HttpMessageTestFixture;
import model.http.TotalHttpMessage;
import model.http.sub.HttpVersion;
import model.http.sub.RequestMethod;
import model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import webserver.handler.response.RequestResourceType;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


public class HttpMessageRoutingIntegrationTest {

    private static ByteArrayOutputStream out;

    @BeforeEach
    void setUp() {
        out = new ByteArrayOutputStream();
    }

    @Test
    void 동적_컨텐츠_요청에_정상적으로_작동한다() {
        // given - [AI]
        TotalHttpMessage msg = HttpMessageTestFixture.createParsedHttpMessage(
                RequestMethod.POST,
                "/user/create",
                Map.of(),
                HttpVersion.HTTP_1_1,
                Map.of("Host", "localhost:8080"),
                "userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net"
        );

        // when & then - [AI]
        assertThat(RequestResourceType.process(out, msg)).isTrue();
        assertThat(out.toString())
                .contains("HTTP/1.1 302 Found")
                .contains("Location:/index.html");

        User savedUser = Database.findUserById("javajigi");

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUserId()).isEqualTo("javajigi");
        assertThat(savedUser.getPassword()).isEqualTo("password");
        assertThat(savedUser.getName()).isEqualTo("박재성");
        assertThat(savedUser.getEmail()).isEqualTo("javajigi@slipp.net");
    }

    @Test
    void index_html을_빼고_요청해도_index_html을_반환한다() {
        // given - [AI]
        TotalHttpMessage msg = HttpMessageTestFixture.createParsedHttpMessage(
                RequestMethod.GET,
                "/login",
                null,
                HttpVersion.HTTP_1_1,
                Map.of("Host", "localhost:8080"),
                null
        );

        // when & then
        assertThat(RequestResourceType.process(out, msg)).isTrue();
        assertThat(out.toString())
                .contains("HTTP/1.1 200 OK")
                .contains("Content-Type:text/html")
                .contains("<!DOCTYPE html>")
                .contains("로그인");
    }

    @Test
    void 정적_파일_요청에_정상적으로_출력한다() {
        // given - [AI]
        TotalHttpMessage msg = HttpMessageTestFixture.createParsedHttpMessage(
                RequestMethod.GET,
                "/registration/index.html",
                null,
                HttpVersion.HTTP_1_1,
                Map.of("Host", "localhost:8080"),
                null
        );

        // when & then
        assertThat(RequestResourceType.process(out, msg)).isTrue();
        assertThat(out.toString())
                .contains("HTTP/1.1 200 OK")
                .contains("Content-Type:text/html")
                .contains("<!DOCTYPE html>")
                .contains("회원가입");
    }

    @Test
    void 동적_정적_컨텐츠_둘다_없는_요청에_대해_거짓을_반환한다() {
        // given - [AI]
        TotalHttpMessage msg = HttpMessageTestFixture.createParsedHttpMessage(
                RequestMethod.GET,
                "/user/error",
                null,
                HttpVersion.HTTP_1_1,
                Map.of("Host", "localhost:8080"),
                null
        );

        // when & then
        assertThat(RequestResourceType.process(out, msg)).isFalse();
    }
}
