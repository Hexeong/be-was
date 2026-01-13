package webserver.handler;

import exception.CustomException;
import exception.ErrorCode;
import util.extractor.FileTypeExtractor;
import model.http.HttpRequest;
import model.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ResourceResponseHandler {
    private static final Logger log = LoggerFactory.getLogger(ResourceResponseHandler.class);

    private static final String CONTENT_TYPE_HEADER_KEY = "Content-Type";

    // File 클래스는 상대 경로 사용 시 실행 위치 기준이므로 ./ 로 시작하는 것이 명확합니다.
    private static final String TEMPLATE_ROOT = "./src/main/resources/static";

    public static void handle(HttpRequest req, HttpResponse res) throws IOException {
        String pathUrl = req.line().getPathUrl();

        if (!StaticResourceType.isStaticResourceByUrl(pathUrl)) {
            log.error("존재하지 않는 URL 매핑 또는 자원 확장자에 접근!");
            throw new CustomException(ErrorCode.NOT_FOUND);
        }

        if (pathUrl.contains("..")) {
            log.error("이전 폴더 접근 문법 사용!");
            throw new CustomException(ErrorCode.NOT_FOUND);
        }

        File file = new File(TEMPLATE_ROOT, pathUrl);

        if (!file.exists() || !file.isFile()) {
            log.error("파일 없음! " + file.getAbsolutePath());
            throw new CustomException(ErrorCode.NOT_FOUND);
        }

        String fileExtension = FileTypeExtractor.extract(pathUrl);
        res.headers().put(CONTENT_TYPE_HEADER_KEY, StaticResourceType.findByType(fileExtension).getContentType());

        byte[] body = new byte[(int) file.length()];

        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(body);
        }

        res.setBody(body);
    }
}
