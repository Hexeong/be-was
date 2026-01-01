package parser.http.impl;

import parser.http.HeadParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpHeaderParser implements HeadParser<Map<String, String>> {

    protected HttpHeaderParser() {}

    public Map<String, String> parse(BufferedReader bufRed) throws IOException {
        String line;
        Map<String, String> headers = new HashMap<>();

        while((line = bufRed.readLine()) != null && !line.isEmpty()) {
            String[] kv = line.split(": ");
            headers.put(kv[0], kv[1]);
        }

        return headers;
    }
}
