package parser.http.impl;

import model.http.HttpStartLine;
import parser.http.HeadParser;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpStartLineParser implements HeadParser<HttpStartLine> {

    protected HttpStartLineParser() {}

    public HttpStartLine parse(BufferedReader bufRed) throws IOException {
        String line = bufRed.readLine();
        String[] tokens = line.split(" ");
        return new HttpStartLine(tokens[0], tokens[1], tokens[2]);
    }
}
