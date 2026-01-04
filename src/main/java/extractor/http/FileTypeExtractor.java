package extractor.http;

public class FileTypeExtractor implements HttpInfoExtractor<String> {

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

    public String extract(String pathUrl) {
        int lastIdxOfDot = pathUrl.lastIndexOf(".");

        if (lastIdxOfDot == -1)
            return ""; // 비교를 위해 빈 문자열을 return

        return pathUrl.substring(lastIdxOfDot + 1);
    }
}
