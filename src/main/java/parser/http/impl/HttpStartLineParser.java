package parser.http.impl;

import extractor.http.QueryParameterExtractor;
import model.http.HttpStartLine;
import parser.http.HeadParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpStartLineParser implements HeadParser<HttpStartLine> {

    protected HttpStartLineParser() {}

    public HttpStartLine parse(BufferedReader bufRed) throws IOException {
        String line = bufRed.readLine();
        String[] tokens = line.split(" ");

        int lastIdx = tokens[1].lastIndexOf("?");

        if (lastIdx == -1)
            return new HttpStartLine(tokens[0], tokens[1], new HashMap<>(), tokens[2]);

        Map<String, Object> queryParameterList =
                QueryParameterExtractor.getInstance().extract(tokens[1].substring(lastIdx + 1));

        return new HttpStartLine(tokens[0], tokens[1].substring(0, lastIdx), queryParameterList, tokens[2]);
    }
}
