package writer;

import model.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class ResponseHeaderWriter {
    private static final Logger log = LoggerFactory.getLogger(ResponseHeaderWriter.class);

    public ResponseHeaderWriter() {}

    public static void writeHeader(DataOutputStream dos, Map<String, String> additionalHeaders,
                            int lengthOfBodyContent, HttpStatus status) {
        try {
            // TODO:: version을 고정하는 게 맞는건가? Client가 1.2로 보냈을 때의 웹 서버 정책은 보통 어떻게 되는 것인가??
            dos.writeBytes("HTTP/1.1 " + status.getCode() + " " + status.getMessage() + " \r\n");
            for (Map.Entry<String, String> header : additionalHeaders.entrySet()) {
                dos.writeBytes( header.getKey() + ": " + header.getValue() + "\r\n");
            }
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
