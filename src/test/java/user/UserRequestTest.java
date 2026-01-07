package user;

import db.Database;
import fixture.HttpMessageTestFixture;
import model.http.TotalHttpMessage;
import model.http.sub.HttpVersion;
import model.http.sub.RequestMethod;
import model.user.User;
import org.junit.jupiter.api.Test;
import routing.TotalRouteMapping;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import static java.lang.System.out;
import static org.assertj.core.api.Assertions.assertThat;

public class UserRequestTest {

    @Test
    void 회원가입_요청에_대하여_POST_요청을_정상적으로_처리한다() {
        // given
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        TotalHttpMessage msg = HttpMessageTestFixture.createParsedHttpMessage(
                RequestMethod.POST,
                "/user/create",
                Map.of(),
                HttpVersion.HTTP_1_1,
                Map.of("Connection", "keep-alive",
                        "Content-Length", "59",
                        "Content-Type", "application/x-www-form-urlencoded",
                        "Accept", "*/*"),
                "userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net");

        // when & then
        assertThat(TotalRouteMapping.route(out, msg)).isTrue();
        assertThat(out.toString())
                .contains("302")
                .contains("Found")
                .contains("Location:/index.html");
    }

    @Test
    void 회원가입_요청에_대하여_GET_요청을_실패한다() {
        // given
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        TotalHttpMessage msg = HttpMessageTestFixture.createParsedHttpMessage(
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
        assertThat(TotalRouteMapping.route(out, msg)).isFalse();
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

        TotalHttpMessage msg = HttpMessageTestFixture.createParsedHttpMessage(
                RequestMethod.POST,
                "/user/login",
                Map.of(),
                HttpVersion.HTTP_1_1,
                Map.of("Connection", "keep-alive",
                        "Content-Length", "59",
                        "Content-Type", "application/x-www-form-urlencoded",
                        "Accept", "*/*"),
                "userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net");

        // when & then
        assertThat(TotalRouteMapping.route(out, msg)).isTrue();
        assertThat(out.toString())
                .contains("302")
                .contains("Found")
                .contains("Location:/index.html");
    }
}
