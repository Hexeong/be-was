package parser.http.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpHeaderParser {

    private static final String HEADER_DELIMITER = ":";

    public HttpHeaderParser() {}

    public static Map<String, String> parse(BufferedReader bufRed) throws IOException {
        String line;
        Map<String, String> headers = new HashMap<>();

        while((line = bufRed.readLine()) != null && !line.isEmpty()) {
            int indexOfFirst = line.indexOf(HEADER_DELIMITER);
            headers.put(line.substring(0, indexOfFirst).toLowerCase(), line.substring(indexOfFirst).trim());
        }

        return headers;
    }
}
