package interceptor;

import annotation.MultipartFormdata;
import exception.CustomException;
import exception.ErrorCode;
import handler.HandlerMethod;
import model.http.HttpRequest;
import model.http.HttpResponse;
import model.http.HttpStatus;
import webserver.handler.UploadableFileType;

import java.lang.reflect.Parameter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUploadSafetyInterceptor implements Interceptor{

    private static final String COOKIE_HEADER_KEY = "Set-Cookie";
    private static final String HEADER_LOCATION = "Location";
    private static final Pattern FILENAME_PATTERN = Pattern.compile("filename=\"(.*?)\"");
    private static final String MY_DOMAIN = "http://localhost:8080";

    @Override
    public boolean preHandle(HttpRequest req, HttpResponse res, Object handler) throws CustomException {
        if (handler instanceof HandlerMethod hm) {
            boolean flag = false;
            for (Parameter params : hm.getHandlerMethod().getParameters()) {
                if (params.isAnnotationPresent(MultipartFormdata.class)) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                Matcher matcher = FILENAME_PATTERN.matcher(req.bodyText());
                while (matcher.find()) {
                    String originalFilename = matcher.group(1);
                    if (originalFilename == null || originalFilename.isBlank()) {
                        continue;
                    }

                    if (!UploadableFileType.isAllowed(originalFilename)) {
                        String redirectPathOnFail = req.headers().getOrDefault("referer", "/");
                        if (!redirectPathOnFail.startsWith(MY_DOMAIN))
                            redirectPathOnFail = "/";

                        res.setStatus(HttpStatus.FOUND);
                        res.addHeader(HEADER_LOCATION, redirectPathOnFail);

                        String encodedMsg = URLEncoder.encode("업로드를 지원하지 않는 이미지 파일 형식입니다.", StandardCharsets.UTF_8);
                        res.addHeader(COOKIE_HEADER_KEY, "alertMessage=" + encodedMsg + "; Path=/");
                        throw new CustomException(ErrorCode.NOT_SUPPORTED_FILE_TYPE);
                    }
                }
            }
        }
        return true;
    };
}
