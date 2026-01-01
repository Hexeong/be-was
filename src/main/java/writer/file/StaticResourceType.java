package writer.file;

import extractor.http.FileTypeExtractor;

public enum StaticResourceType {
    HTML("html", "text/html; charset=utf-8"),
    CSS("css", "text/css"),
    JS("js", "text/javascript"),
    ICO("ico", "image/x-icon"),
    PNG("png", "image/png"),
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
            if (staticResourceType.fileExtension.equals(type))
                return staticResourceType;
        }
        throw new IllegalArgumentException("Not Found FileType By " + type);
    }

    public static String isStaticResourceByUrl(String pathUrl) {
        String type = FileTypeExtractor.getInstance().extract(pathUrl);

        for (StaticResourceType staticResourceType : StaticResourceType.values()) {
            if (staticResourceType.fileExtension.equals(type))
                return "static";
        }
        return "dynamic";
    }

    public String getContentType() {
        return contentType;
    }
}
