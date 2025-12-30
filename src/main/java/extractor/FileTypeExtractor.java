package extractor;

public class FileTypeExtractor {

    private static volatile FileTypeExtractor instance = null;

    private FileTypeExtractor() {}

    public static FileTypeExtractor getInstance() {
        if (instance == null) {
            synchronized (FileTypeExtractor.class) {
                if (instance == null) {
                    instance = new FileTypeExtractor();
                }
            }
        }
        return instance;
    }

    public String extractFileExtensionFromURL(String url) {
        String fullUrl = url;

        int lastIdxOfDot = fullUrl.lastIndexOf(".");

        if (lastIdxOfDot == -1)
            return ""; // 비교를 위해 빈 문자열을 return

        return url.substring(lastIdxOfDot + 1);
    }
}
