package user;

import business.BusinessHandler;
import db.Database;
import fixture.HttpMessageTestFixture;
import model.http.HttpRequest;
import model.http.HttpResponse;
import model.http.sub.HttpVersion;
import model.http.sub.RequestMethod;
import model.user.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import routing.TotalRouteMapping;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class UserRequestTest {

    @Test
    void 회원가입_요청에_대하여_POST_요청을_정상적으로_처리한다() {
        // given
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        HttpRequest req = HttpMessageTestFixture.createParsedHttpMessage(
                RequestMethod.POST,
                "/user/create",
                Map.of(),
                HttpVersion.HTTP_1_1,
                Map.of("Connection", "keep-alive",
                        "Content-Length", "59",
                        "Content-Type", "application/x-www-form-urlencoded",
                        "Accept", "*/*"),
                "userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net");
        HttpResponse res = new HttpResponse();

        // when
        BusinessHandler handler = TotalRouteMapping.route(req);

        // then
        Assertions.assertNotNull(handler);
        Assertions.assertDoesNotThrow(() -> handler.execute(req, res).resolve(req, res));
        res.sendResponse(out);
        assertThat(out.toString())
                .contains("302")
                .contains("Found")
                .contains("Location:/");
    }

    @Test
    void 회원가입_요청에_대하여_GET_요청을_실패한다() {
        // given
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        HttpRequest req = HttpMessageTestFixture.createParsedHttpMessage(
                RequestMethod.GET,
                "/user/create",
                Map.of(),
                HttpVersion.HTTP_1_1,
                Map.of("Connection", "keep-alive",
                        "Content-Length", "59",
                        "Content-Type", "application/x-www-form-urlencoded",
                        "Accept", "*/*"),
                "userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net");

        // when & then
        assertThat(TotalRouteMapping.route(req)).isNull();
    }

    @Test
    void 로그인_요청에_대하여_POST_요청을_성공한다() {
        // given
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Database.addUser(new User(
                "javajigi",
                "password",
                "박유성",
                "javajigi@slipp.net"));

        HttpRequest req = HttpMessageTestFixture.createParsedHttpMessage(
                RequestMethod.POST,
                "/user/login",
                Map.of(),
                HttpVersion.HTTP_1_1,
                Map.of("Connection", "keep-alive",
                        "Content-Length", "59",
                        "Content-Type", "application/x-www-form-urlencoded",
                        "Accept", "*/*"),
                "userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net");
        HttpResponse res = new HttpResponse();

        // when
        BusinessHandler handler = TotalRouteMapping.route(req);

        // when & then
        Assertions.assertNotNull(handler);
        Assertions.assertDoesNotThrow(() -> handler.execute(req, res).resolve(req, res));
        res.sendResponse(out);
        assertThat(out.toString())
                .contains("302")
                .contains("Found")
                .contains("Location:/");
    }
}
