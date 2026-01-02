package parser.http.impl;

import extractor.http.QueryParameterExtractor;
import model.http.HttpStartLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpStartLineParser {

    private static final String SP = " ";
    private static final String QUERY_STRING_DELIMITER = "?";

    public HttpStartLineParser() {}

    public static HttpStartLine parse(BufferedReader bufRed) throws IOException {
        String line = bufRed.readLine();
        if (line == null || line.isBlank()) {
            throw new IOException("Invalid or Empty Request Line");
        }
        String[] tokens = line.split(SP);

        if (tokens.length != 3) {
            throw new IllegalArgumentException("Invalid Request Line format: " + line);
        }

        String method = tokens[0];
        String targetUrl = tokens[1]; // path + query string
        String version = tokens[2];

        int lastIdx = targetUrl.indexOf(QUERY_STRING_DELIMITER);

        if (lastIdx == -1)
            return new HttpStartLine(method, targetUrl, new HashMap<>(), version);

        Map<String, Object> queryParameterList =
                QueryParameterExtractor.getInstance().extract(tokens[1].substring(lastIdx + 1));

        return new HttpStartLine(method, targetUrl.substring(0, lastIdx), queryParameterList, version);
    }
}
