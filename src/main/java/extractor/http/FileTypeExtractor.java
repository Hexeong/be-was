package extractor.http;

public class FileTypeExtractor {

    private FileTypeExtractor() {}

    public static String extract(String pathUrl) {
        int lastIdxOfDot = pathUrl.lastIndexOf(".");

        if (lastIdxOfDot == -1)
            return ""; // 비교를 위해 빈 문자열을 return

        return pathUrl.substring(lastIdxOfDot + 1);
    }
}
