package webserver.handler;

import util.extractor.FileTypeExtractor;

import java.util.Arrays;

public enum StaticResourceType {
    // TODO:: MimeType으로 변경해서 통합 관리 필요
    HTML("html", "text/html; charset=utf-8"),
    CSS("css", "text/css"),
    JS("js", "text/javascript"),
    ICO("ico", "image/x-icon"),
    PNG("png", "image/png"),
    JPEG("jpeg", "image/jpeg"),
    JPG("jpg", "image/jpeg"),
    SVG("svg", "image/svg+xml");

    private final String fileExtension;
    private final String contentType;

    StaticResourceType(String extension, String contentType) {
        this.fileExtension = extension;
        this.contentType = contentType;
    }

    public static StaticResourceType findByType(String type) {
        for (StaticResourceType staticResourceType : StaticResourceType.values()) {
            if (staticResourceType.fileExtension.equals(type.toLowerCase())) {
                return staticResourceType;
            }
        }
        throw new IllegalArgumentException("Not Found FileType By " + type);
    }

    public static boolean isStaticResourceByUrl(String pathUrl) {
        String type = FileTypeExtractor.extract(pathUrl);

        return Arrays.stream(StaticResourceType.values())
                .anyMatch(resourceType -> resourceType.fileExtension.equals(type.toLowerCase()));
    }

    public String getContentType() {
        return contentType;
    }
}
