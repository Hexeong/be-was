package parser.http;

import model.http.HttpHeader;

import java.io.BufferedReader;
import java.io.IOException;

public interface ContentParser<T> {
    T parse(BufferedReader bufRed, HttpHeader header) throws IOException;
}
