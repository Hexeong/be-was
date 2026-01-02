package parser;

import fixture.HttpMessageTestFixture;
import model.http.TotalHttpMessage;
import model.http.sub.HttpVersion;
import model.http.sub.RequestMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import parser.http.HttpParserFacade;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class HttpMessageParserIntegrationTest {

    private List<HttpRequestTestCase> testCases; // [AI를 사용해 테스트 값 생성]

    @BeforeEach
    void setUp() {
        testCases = new ArrayList<>();

        // ----------------------------------------------------------------
        // 1. [Simple GET] 정적 파일 요청
        // ----------------------------------------------------------------
        testCases.add(new HttpRequestTestCase(
                "Simple GET Request",
                RequestMethod.GET,
                "/index.html",
                new HashMap<>(), // 빈 파라미터
                Map.of("Host", "localhost:8080", "Connection", "keep-alive"),
                null // Body 없음
        ));

        // ----------------------------------------------------------------
        // 2. [GET with Params] 쿼리 파라미터 포함
        // ----------------------------------------------------------------
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("keyword", "spring");
        queryParams.put("page", "1");
        queryParams.put("filter", Arrays.asList("new", "hot"));

        testCases.add(new HttpRequestTestCase(
                "GET with Query Parameters",
                RequestMethod.GET,
                "/search",
                queryParams,
                Map.of("Host", "localhost:8080"),
                null
        ));

        // ----------------------------------------------------------------
        // 3. [POST with JSON] JSON Body
        // ----------------------------------------------------------------
        String jsonBody = "{\"username\": \"javajigi\", \"age\": 20}";
        testCases.add(new HttpRequestTestCase(
                "POST with JSON Body",
                RequestMethod.POST,
                "/api/users",
                null,
                Map.of("Host", "localhost:8080", "Content-Type", "application/json"),
                jsonBody
        ));

        // ----------------------------------------------------------------
        // 4. [POST with Form] Form Data
        // ----------------------------------------------------------------
        String formData = "userId=javajigi&password=password123&name=JaeSung";
        testCases.add(new HttpRequestTestCase(
                "POST with Form Data",
                RequestMethod.POST,
                "/user/create",
                null,
                Map.of("Host", "localhost:8080", "Content-Type", "application/x-www-form-urlencoded"),
                formData
        ));
    }

    @Test
    void 모든_요청을_정확히_파싱한다() {
        for (HttpRequestTestCase testCase : testCases) {
            System.out.println("Testing case: " + testCase.description);

            // given & when: 파싱 수행
            TotalHttpMessage result;
            try {
                result = HttpParserFacade.parse(testCase.inputStream);
            } catch (IOException e) {
                throw new RuntimeException("Parsing failed for case: " + testCase.description, e);
            }

            // then
            assertAll(
                    // Method 검증
                    () -> assertThat(result.line().getMethod()).isEqualTo(testCase.expectedMethod),
                    // Path 검증 (경로만 비교, 쿼리 스트링 제외 로직이 있다면 주의)
                    () -> assertThat(result.line().getPathUrl()).isEqualTo(testCase.expectedPath),
                    // Header 검증 (Map에 있는 키들이 포함되어 있는지)
                    () -> assertThat(result.headers()).containsAllEntriesOf(testCase.expectedHeaders),
                    // Header 검증 (Map에 있는 키들이 값이 제대로 포함되어 있는지)
                    () -> result.headers().forEach((key, value) -> {
                        assertThat(result.headers().get(key)).isEqualTo(value);
                    })
            );

            // 4. Parameter 검증 (파라미터가 있는 경우만)
            if (testCase.expectedParams != null && !testCase.expectedParams.isEmpty()) {
                Map<String, Object> actualParams = result.line().getQueryParameterList();

                testCase.expectedParams.forEach((key, value) -> {
                    assertThat(actualParams).containsKey(key);
                    assertThat(actualParams.get(key)).isEqualTo(value);
                });
            }

            // 5. Body 검증 (Body가 있는 경우만)
            if (testCase.expectedBody != null) {
                assertThat(result.body().bodyText()).isEqualTo(testCase.expectedBody);
            }
        }
    }

    // 테스트 데이터와 기대값을 함께 들고 있는 클래스 (Helper Class) - [AI를 통해 헬퍼 클래스 생성]
    private static class HttpRequestTestCase {
        String description;
        RequestMethod expectedMethod;
        String expectedPath;
        Map<String, Object> expectedParams;
        Map<String, String> expectedHeaders;
        String expectedBody;
        InputStream inputStream;

        public HttpRequestTestCase(String description, RequestMethod method, String path,
                                   Map<String, Object> params, Map<String, String> headers, String body) {
            this.description = description;
            this.expectedMethod = method;
            this.expectedPath = path;
            this.expectedParams = params;
            this.expectedHeaders = headers;
            this.expectedBody = body;

            this.inputStream = HttpMessageTestFixture.createRawByteHttpMessage(
                    method, path, params, HttpVersion.HTTP_1_1, headers, body
            );
        }
    }
}