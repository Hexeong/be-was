package resolver.view;

import model.Model;
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

    public void resolve(HttpResponse res) throws IOException {
        // 여기서는 byte[]를 만들어야 함. viewName을 보고 File을 읽어온 다음, Model에 있는 값에 대해 적용하는 방식
        if (viewName.startsWith(REDIRECT_IDENTIFIER)) {
            String redirectPath = viewName.substring(REDIRECT_IDENTIFIER.length());
            res.setStatus(HttpStatus.FOUND);
            res.addHeader(REDIRECT_HEADER_KEY, redirectPath);
            return;
        }

        res.addHeader(CONTENT_TYPE_HEADER_KEY, StaticResourceType.HTML.getContentType());
        res.setStatus(HttpStatus.OK);
        renderView(res, this.viewName);
    }

    private void renderView(HttpResponse res, String pathName) throws IOException {
        if (!pathName.endsWith(".html")) {
            pathName += ".html";
        }

        Path path = Paths.get(TEMPLATE_ROOT, pathName);
        if (!Files.exists(path)) {
            res.setStatus(HttpStatus.NOT_FOUND);
            return;
        }

        byte[] fileBytes = Files.readAllBytes(path);

        StringBuilder sb = new StringBuilder(new String(fileBytes, StandardCharsets.UTF_8));

        // 반복문 처리 (<for> 태그)
        SimpleTemplateEngine.renderLoops(sb, model);
        // 조건문 처리 (<if> 태그)
        SimpleTemplateEngine.renderConditionals(sb, model);
        // 변수 치환 ({{ ... }})
        String finalContent = SimpleTemplateEngine.renderVariables(sb, model);

        // 응답 전송
        byte[] finalBytes = finalContent.getBytes(StandardCharsets.UTF_8);
        res.setBody(finalBytes);
    }
}
