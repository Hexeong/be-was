package parser.http.implement;

import model.ParsedHttpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

public class BufferedReaderHttpParser {
    private static final Logger log = LoggerFactory.getLogger(BufferedReaderHttpParser.class);

    private static BufferedReaderHttpParser instance = null;

    public BufferedReaderHttpParser() {
    }

    public static BufferedReaderHttpParser getInstance() {
        if (instance == null) {
            synchronized (instance) {
                if (instance == null)
                    instance = new BufferedReaderHttpParser();
            }
        }
        return instance;
    }

    public ParsedHttpMessage parse(BufferedReader bufRed) {
        try {
            // Http Method와 URL 구분

            String line = bufRed.readLine();
            String[] tokens = line.split(" ");
            log.debug(tokens[0] + " " + tokens[1]);

            // Header HashMap 만들기
            HashMap<String, String> headers = new HashMap<>();
            while ((line = bufRed.readLine()) != null &&
                !line.isEmpty()) {

                String[] keyAndValue = line.split(": ");
                headers.put(keyAndValue[0], keyAndValue[1]);
                log.debug("1");
            }
            log.debug("1-1");

            // Body String 만들기
            StringBuilder stringBuilder = new StringBuilder();
            log.debug("2-1");
            if (headers.containsKey("Content-Length")) {
                int contentLength = Integer.parseInt(headers.get("Content-Length"));
                char[] bodyChars = new char[contentLength];
                bufRed.read(bodyChars, 0, contentLength);

                stringBuilder.append(bodyChars);
                log.debug("2: Body read complete");
            }
            // GET 요청의 경우 Body가 비워져 있기에 빈 줄까지만 보내고 데이터를 보내지 않는다.
            // 하지만, socket 연결의 경우 끊기지 않고 계속 유지되기에 계속 다음 데이터를 대기한 채 readLine()에 Block되어 기다리는 것이다
//            while ((line = bufRed.readLine()) != null) {
//
//                stringBuilder.append(line).append(System.lineSeparator());
//                log.debug("2");
//            }

            log.debug("body: " + stringBuilder.toString());

            return new ParsedHttpMessage(
                    tokens[0],
                    tokens[1],
                    headers,
                    stringBuilder.toString()
                );
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return null;
    }


}