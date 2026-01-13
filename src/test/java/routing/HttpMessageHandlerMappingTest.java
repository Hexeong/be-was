package routing;

import db.Database;
import exception.CustomException;
import fixture.HttpMessageTestFixture;
import handler.HandlerExecutionChain;
import handler.HandlerMethod;
import handler.adapter.HandlerAdapter;
import model.http.HttpRequest;
import model.http.HttpResponse;
import model.http.sub.HttpVersion;
import model.http.sub.RequestMethod;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import resolver.view.ModelAndView;
import webserver.ApplicationContext;
import webserver.handler.ResourceResponseHandler;

import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class HttpMessageHandlerMappingTest {

    private ApplicationContext context;
    private ByteArrayOutputStream out;

    @BeforeEach
    void setUp() throws SQLException {
        // ApplicationContext 생성 시 컴포넌트 스캔 및 매핑 초기화가 진행됩니다.
        context = new ApplicationContext();
        out = new ByteArrayOutputStream();
    }

    @Test
    void 동적_컨텐츠인_회원가입_요청_시_적절한_HandlerMethod를_찾고_실행하여_리다이렉트한다() {
        // given
        HttpRequest req = HttpMessageTestFixture.createParsedHttpMessage(
                RequestMethod.POST,
                "/user/create",
                Map.of(),
                HttpVersion.HTTP_1_1,
                Map.of("Host", "localhost:8080"),
                "userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net"
        );
        HttpResponse res = new HttpResponse(req, out);

        // when
        // 1. 요청을 처리할 핸들러(체인) 조회
        HandlerExecutionChain chain = context.getHandler(req);
        Object handler = chain.getHandler();

        // 2. 핸들러를 수행할 어댑터 조회
        HandlerAdapter adapter = context.getHandlerAdapter(handler);

        // 3. 실행
        ModelAndView mv = adapter.handle(req, res, handler);

        // then
        assertAll(
                // 1. 핸들러가 정상적으로 조회되었는지 검증
                () -> assertThat(chain).isNotNull(),
                () -> assertThat(handler).isInstanceOf(HandlerMethod.class),

                // 2. 실행 결과(ViewName) 검증
                () -> assertThat(mv.viewName()).isEqualTo("redirect:/"),

                // 3. 사이드 이펙트(DB 저장) 검증
                () -> {
                    User savedUser = Database.findUserById("javajigi");
                    assertThat(savedUser).isNotNull();
                    assertThat(savedUser.getName()).isEqualTo("박재성");
                    assertThat(savedUser.getEmail()).isEqualTo("javajigi@slipp.net");
                }
        );
    }

    @Test
    void index파일_요청시_index_html을_제외한_path로_성공적으로_처리된다() {
        // given
        HttpRequest req = HttpMessageTestFixture.createParsedHttpMessage(
                RequestMethod.GET,
                "/registration/index.html",
                null,
                HttpVersion.HTTP_1_1,
                Map.of("Host", "localhost:8080"),
                null
        );
        HttpResponse res = new HttpResponse(req, out);

        // when
        // 정적 파일은 @RequestMapping에 등록되지 않았으므로 null이어야 함
        HandlerExecutionChain chain = context.getHandler(req);
        Object handler = chain.getHandler();

        // 2. 핸들러를 수행할 어댑터 조회
        HandlerAdapter adapter = context.getHandlerAdapter(handler);
        ModelAndView mv = adapter.handle(req, res, handler);

        // then
        // 1. 매핑된 동적 핸들러가 없음을 검증
        assertThat(chain).isNotNull();
        assertDoesNotThrow(() -> mv.resolve(res));

        res.sendResponse();

        assertThat(out.toString())
                .contains("HTTP/1.1 200 OK")
                .contains("Content-Type:text/html") // 띄어쓰기 주의
                .contains("회원가입");
    }

    @Test
    void 존재하지_않는_URL_요청에_대해_핸들러를_찾지_못한다_null_반환() {
        // given
        HttpRequest req = HttpMessageTestFixture.createParsedHttpMessage(
                RequestMethod.GET,
                "/user/error-page-not-found", // 존재하지 않는 경로
                null,
                HttpVersion.HTTP_1_1,
                Map.of("Host", "localhost:8080"),
                null
        );
        HttpResponse res = new HttpResponse(req, out);

        // when
        HandlerExecutionChain chain = context.getHandler(req);

        // then
        assertThat(chain).isNull();
        assertThrows(CustomException.class, () -> ResourceResponseHandler.handle(req, res));
    }
}