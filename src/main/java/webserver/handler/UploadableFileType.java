package webserver.handler;

import java.util.Arrays;

public enum UploadableFileType {
    JPG("jpg"),
    JPEG("jpeg"),
    PNG("png"),
    GIF("gif"),
    WEBP("webp"),
    SVG("svg");

    private final String extension;

    UploadableFileType(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public static boolean isAllowed(String filename) {
        if (filename == null || filename.isBlank()) {
            return false;
        }

        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex == -1) {
            return false;
        }

        String ext = filename.substring(dotIndex + 1).toLowerCase();

        return Arrays.stream(values())
                .anyMatch(type -> type.extension.equals(ext));
    }
}