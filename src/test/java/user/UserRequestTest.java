package user;

import db.Database;
import fixture.HttpMessageTestFixture;
import handler.HandlerExecutionChain;
import handler.adapter.HandlerAdapter;
import model.http.HttpRequest;
import model.http.HttpResponse;
import model.http.sub.HttpVersion;
import model.http.sub.RequestMethod;
import model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import resolver.view.ModelAndView;
import webserver.ApplicationContext;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class UserRequestTest {

    private ApplicationContext context;
    private ByteArrayOutputStream out;

    @BeforeEach
    void setUp() {
        context = new ApplicationContext();
        out = new ByteArrayOutputStream();
    }

    @Test
    void 회원가입_요청에_대하여_POST_요청을_정상적으로_처리한다() {
        // given
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
        HttpResponse res = new HttpResponse(out);

        // when
        HandlerExecutionChain chain = context.getHandler(req);
        HandlerAdapter adapter = context.getHandlerAdapter(chain.getHandler());
        ModelAndView mv = adapter.handle(req, res, chain.getHandler());

        // then
        assertAll(
                () -> assertThat(mv.viewName()).isEqualTo("redirect:/"),
                () -> {
                    User savedUser = Database.findUserById("javajigi");
                    assertThat(savedUser).isNotNull();
                    assertThat(savedUser.getName()).isEqualTo("박재성");
                }
        );
    }

    @Test
    void 회원가입_요청에_대하여_GET_요청을_실패한다() {
        // given
        HttpRequest req = HttpMessageTestFixture.createParsedHttpMessage(
                RequestMethod.GET,
                "/user/create", // POST로만 매핑되어 있다고 가정
                Map.of(),
                HttpVersion.HTTP_1_1,
                Map.of("Connection", "keep-alive",
                        "Content-Length", "59",
                        "Content-Type", "application/x-www-form-urlencoded",
                        "Accept", "*/*"),
                "userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net");

        // when
        HandlerExecutionChain chain = context.getHandler(req);

        // then
        // 해당 URL+Method 조합에 맞는 핸들러가 없으므로 null 반환
        assertThat(chain).isNull();
    }

    @Test
    void 로그인_요청에_대하여_POST_요청을_성공한다() {
        // given
        Database.addUser(new User(
                "javajigi",
                "password",
                "박재성",
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
        HttpResponse res = new HttpResponse(out);

        // when
        HandlerExecutionChain chain = context.getHandler(req);
        HandlerAdapter adapter = context.getHandlerAdapter(chain.getHandler());
        ModelAndView mv = adapter.handle(req, res, chain.getHandler());

        // then
        assertThat(chain).isNotNull();
        assertThat(mv.viewName()).isEqualTo("redirect:/");
    }
}