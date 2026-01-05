package parser.http.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class HttpHeaderParser {

    private static final String HEADER_DELIMITER = ":";
    private static final String CR = "\r";
    private static final char LF = '\n';
    private static final Pattern ALLOWED_HEADER_PATTERN = Pattern.compile("^[\\x09\\x20-\\x7E]*$");

    private HttpHeaderParser() {}

    public static Map<String, String> parse(InputStream inputStream) throws IOException {
        String line;
        Map<String, String> headers = new HashMap<>();

        while ((line = readHeaderLine(inputStream)) != null && !line.isEmpty()) {

            if (!ALLOWED_HEADER_PATTERN.matcher(line).matches()) {
                throw new IOException("Invalid Character detected in Header: " + line);
            }

            int indexOfFirst = line.indexOf(HEADER_DELIMITER);
            if (indexOfFirst != -1) {
                String k = line.substring(0, indexOfFirst).trim().toLowerCase();
                String v = line.substring(indexOfFirst + 1).trim();
                headers.put(k, v);
            }
        }

        return headers;
    }

    /** [AI로 작성]
     * InputStream에서 한 줄(CRLF 또는 LF)을 읽어 문자열로 반환합니다.
     * 이 메서드는 BufferedReader의 버퍼링으로 인한 Body 데이터 침범 문제를 방지합니다.
     */
    private static String readHeaderLine(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int b;

        while ((b = inputStream.read()) != -1) {
            if (b == LF) {
                break;
            }
            buffer.write(b);
        }

        if (buffer.size() == 0 && b == -1) {
            return null; // 스트림 종료
        }

        String line = buffer.toString(StandardCharsets.US_ASCII);

        if (line.endsWith(CR)) {
            return line.substring(0, line.length() - 1);
        }

        return line;
    }
}
