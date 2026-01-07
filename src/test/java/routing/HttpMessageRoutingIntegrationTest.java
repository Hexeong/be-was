package routing;

import business.BusinessHandler;
import db.Database;
import fixture.HttpMessageTestFixture;
import model.http.HttpRequest;
import model.http.HttpResponse;
import model.http.sub.HttpVersion;
import model.http.sub.RequestMethod;
import model.user.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import webserver.handler.ResourceResponseHandler;

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
    void 동적_컨텐츠_요청에_정상적으로_라우팅한다() {
        // given - [AI]
        HttpRequest req = HttpMessageTestFixture.createParsedHttpMessage(
                RequestMethod.POST,
                "/user/create",
                Map.of(),
                HttpVersion.HTTP_1_1,
                Map.of("Host", "localhost:8080"),
                "userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net"
        );
        HttpResponse res = new HttpResponse();

        // when
        BusinessHandler handler = TotalRouteMapping.route(req);

        // then
        Assertions.assertNotNull(handler);
        assertThat(handler.execute(req, res).viewName()).isEqualTo("redirect:/");

        User savedUser = Database.findUserById("javajigi");

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUserId()).isEqualTo("javajigi");
        assertThat(savedUser.getPassword()).isEqualTo("password");
        assertThat(savedUser.getName()).isEqualTo("박재성");
        assertThat(savedUser.getEmail()).isEqualTo("javajigi@slipp.net");
    }

    @Test
    void 정적_파일_요청에_정상적으로_출력한다() {
        // given - [AI]
        HttpRequest req = HttpMessageTestFixture.createParsedHttpMessage(
                RequestMethod.GET,
                "/registration/index.html",
                null,
                HttpVersion.HTTP_1_1,
                Map.of("Host", "localhost:8080"),
                null
        );
        HttpResponse res = new HttpResponse();

        // when & then
        Assertions.assertDoesNotThrow(() -> ResourceResponseHandler.handle(req, res));
        res.sendResponse(out);
        assertThat(out.toString())
                .contains("HTTP/1.1 200 OK")
                .contains("Content-Type:text/html")
                .contains("<!DOCTYPE html>")
                .contains("회원가입");
    }

    @Test
    void 동적_정적_컨텐츠_둘다_없는_요청에_대해_거짓을_반환한다() {
        // given - [AI]
        HttpRequest req = HttpMessageTestFixture.createParsedHttpMessage(
                RequestMethod.GET,
                "/user/error",
                null,
                HttpVersion.HTTP_1_1,
                Map.of("Host", "localhost:8080"),
                null
        );
        HttpResponse res = new HttpResponse();

        // when & then
        Assertions.assertNull(TotalRouteMapping.route(req));
        Assertions.assertDoesNotThrow(() -> ResourceResponseHandler.handle(req, res));
        res.sendResponse(out);
        assertThat(out.toString())
                .contains("HTTP/1.1 404 Not Found");
    }
}
