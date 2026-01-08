package webserver.handler;

import extractor.http.FileTypeExtractor;
import model.http.HttpRequest;
import model.http.HttpResponse;
import model.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ResourceResponseHandler {
    private static final Logger log = LoggerFactory.getLogger(ResourceResponseHandler.class);

    private static final String CONTENT_TYPE_HEADER_KEY = "Content-Type";

    private static final String TEMPLATE_ROOT = "./src/main/resources/static";

    public static void handle(HttpRequest req, HttpResponse res) throws IOException {
        if (!StaticResourceType.isStaticResourceByUrl(req.line().getPathUrl())) {
            req.line().addIndexHtml();
        }

        if (req.line().getPathUrl().contains("..")) {
            // 404 처리
            log.error("이전 폴더 접근 문법 사용!");
            res.setStatus(HttpStatus.NOT_FOUND);
            return;
        }

        Path path = Paths.get(TEMPLATE_ROOT, req.line().getPathUrl());
        if (!Files.exists(path)) {
            // 404 처리
            log.error("파일 없음! " + path);
            res.setStatus(HttpStatus.NOT_FOUND);
            return;
        }

        String fileExtension = FileTypeExtractor.extract(req.line().getPathUrl());
        res.headers().put(CONTENT_TYPE_HEADER_KEY, StaticResourceType.findByType(fileExtension).getContentType());


        res.setBody(Files.readAllBytes(path));
    }
}
