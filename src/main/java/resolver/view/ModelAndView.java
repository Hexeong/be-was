package resolver.view;

import model.Model;
import model.http.HttpRequest;
import model.http.HttpResponse;
import model.http.HttpStatus;
import webserver.handler.StaticResourceType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public record ModelAndView(
        Model model,
        String viewName
) {

    private static final String REDIRECT_IDENTIFIER = "redirect:";

    private static final String REDIRECT_HEADER_KEY = "Location";
    private static final String CONTENT_TYPE_HEADER_KEY = "Content-Type";

    private static final String TEMPLATE_ROOT = "./src/main/resources/static";

    public ModelAndView(String viewName) {
        this(
                new Model(),
                viewName
        );
    }

    public void resolve(HttpRequest req, HttpResponse res) throws IOException {
        // 여기서는 byte[]를 만들어야 함.
        // viewName을 보고 File을 읽어온 다음, Model에 있는 값에 대해 적용하는 방식
        if (viewName.startsWith(REDIRECT_IDENTIFIER)) {
            String redirectPath = viewName.substring(REDIRECT_IDENTIFIER.length());
            res.headers().put(REDIRECT_HEADER_KEY, redirectPath);
            res.setStatus(HttpStatus.FOUND);
            res.setVersion(req.line().getVersion());
            return;
        }

        // viewName으로 경로를 보고 FileReader.readFile(String filePath)로 읽어온 뒤 Model 값에 따라 적용
        // 일반 뷰 렌더링 (HTML 읽어서 동적 처리)
        renderView(res);
    }

    private void renderView(HttpResponse res) throws IOException {
        // 파일 확장자가 없으면 붙여줌
        String pathName = viewName;
        if (!pathName.endsWith(".html")) {
            pathName += ".html";
        }

        // 파일 읽기
        Path path = Paths.get(TEMPLATE_ROOT, pathName);
        if (!Files.exists(path)) {
            // 404 처리
            res.setStatus(HttpStatus.NOT_FOUND);
            return;
        }

        byte[] fileBytes = Files.readAllBytes(path);

        // StringBuilder로 변환 (편집용)
        StringBuilder sb = new StringBuilder(new String(fileBytes, StandardCharsets.UTF_8));

        // 템플릿 엔진 동작 (순서: 조건문 처리 -> 변수 치환)
        // 조건문에 의해 제거될 블록 안에 변수가 있을 수 있으므로 조건문을 먼저 처리하는 게 효율적입니다.
        SimpleTemplateEngine.renderConditionals(sb, model); // <if> 처리
        String finalContent = SimpleTemplateEngine.renderVariables(sb, model); // {{}} 처리

        // 응답 전송
        byte[] finalBytes = finalContent.getBytes(StandardCharsets.UTF_8);
        res.setBody(finalBytes);
        res.headers().put(CONTENT_TYPE_HEADER_KEY, StaticResourceType.HTML.getContentType());
        res.setStatus(HttpStatus.OK);
    }
}
