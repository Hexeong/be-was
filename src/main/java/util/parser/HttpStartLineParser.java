package util.parser;

import util.extractor.QueryParameterExtractor;
import model.http.HttpStartLine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class HttpStartLineParser {

    private static final String SP = " ";
    private static final String QUERY_STRING_DELIMITER = "?";
    private static final Pattern ALLOWED_START_LINE_PATTERN = Pattern.compile("^[\\x20-\\x7E]+$");

    private HttpStartLineParser() {}

    public static HttpStartLine parse(InputStream inputStream) throws IOException {

        String line = readRequestLine(inputStream);

        if (line == null || line.isBlank()) {
            throw new IOException("Invalid or Empty Request Line");
        }

        if (!ALLOWED_START_LINE_PATTERN.matcher(line).matches()) {
            throw new IOException("Invalid Character detected in Request Line (Potential Attack)");
        }

        String[] tokens = line.split(SP);

        if (tokens.length != 3) {
            throw new IllegalArgumentException("Invalid Request Line format: " + line);
        }

        String method = tokens[0];
        String targetUrl = tokens[1];
        String version = tokens[2];

        int lastIdx = targetUrl.indexOf(QUERY_STRING_DELIMITER);

        if (lastIdx == -1)
            return new HttpStartLine(method, targetUrl, new HashMap<>(), version);

        Map<String, Object> queryParameterList =
                QueryParameterExtractor.extract(tokens[1].substring(lastIdx + 1));

        return new HttpStartLine(method, targetUrl.substring(0, lastIdx), queryParameterList, version);
    }

    /** [AI로 멀티바이트 문자 공격 검출 로직 작성]
     * InputStream에서 한 줄(CRLF 또는 LF 만날 때까지)을 읽어 String으로 반환합니다.
     * 이 방식은 BufferedReader와 달리 버퍼링을 하지 않아, 이후 Body 파싱 시 데이터 침범을 막습니다.
     */
    private static String readRequestLine(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int b;

        // 한 바이트씩 읽으면서 줄바꿈(\n)을 찾습니다.
        while ((b = inputStream.read()) != -1) {
            // LF(\n)를 만나면 읽기 중단 (HTTP 표준 줄바꿈)
            if (b == '\n') {
                break;
            }
            buffer.write(b);
        }

        // 스트림이 비어있었다면 null 반환 (BufferedReader.readLine()과 동일한 동작)
        if (buffer.size() == 0 && b == -1) {
            return null;
        }

        // 바이트를 문자열로 변환 (HTTP Request Line은 US-ASCII 표준)
        String line = buffer.toString(StandardCharsets.US_ASCII);

        // 만약 CRLF(\r\n)로 끝났다면, 끝에 남은 CR(\r)을 제거
        if (line.endsWith("\r")) {
            return line.substring(0, line.length() - 1);
        }

        return line;
    }
}
