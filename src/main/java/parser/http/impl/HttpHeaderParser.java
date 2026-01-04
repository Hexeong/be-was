package parser.http.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class HttpHeaderParser {

    private static final String HEADER_DELIMITER = ":";
    // 허용 범위: SP(32) ~ ~(126) + HTAB(9) (헤더는 탭을 포함할 수 있음) [AI로 작성]
    private static final Pattern ALLOWED_HEADER_PATTERN = Pattern.compile("^[\\x09\\x20-\\x7E]*$");

    private HttpHeaderParser() {}

    public static Map<String, String> parse(BufferedReader bufRed) throws IOException {
        String line;
        Map<String, String> headers = new HashMap<>();

        while((line = bufRed.readLine()) != null && !line.isEmpty()) {
            if (!ALLOWED_HEADER_PATTERN.matcher(line).matches()) {
                throw new IOException("Invalid Character detected in Header: " + line);
            }

            int indexOfFirst = line.indexOf(HEADER_DELIMITER);
            headers.put(line.substring(0, indexOfFirst).toLowerCase(), line.substring(indexOfFirst).trim());
        }

        return headers;
    }
}
