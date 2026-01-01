package parser.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public interface ContentParser<T> {
    T parse(BufferedReader bufRed, Map<String, String> header) throws IOException;
}
