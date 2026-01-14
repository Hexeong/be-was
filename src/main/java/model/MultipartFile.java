package model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MultipartFile {

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    private final String originalFilename;
    private final String contentType;
    private final byte[] content;

    public MultipartFile(String originalFilename, String contentType, byte[] content) {
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.content = content;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getContent() {
        return content;
    }

    public String getExtension() {
        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return ""; // 확장자가 없는 경우
    }
    public void saveFileAs(String name) {
        String extension = getExtension();
        File destFile = new File(UPLOAD_DIR, name + extension);
        saveToFile(destFile);
    }

    private void saveToFile(File file) {
        try {
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(this.content);
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패: " + file.getAbsolutePath(), e);
        }
    }
}