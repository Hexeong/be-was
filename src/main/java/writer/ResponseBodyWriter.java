package writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;

public class ResponseBodyWriter {
    private static final Logger log = LoggerFactory.getLogger(ResponseBodyWriter.class);

    public ResponseBodyWriter() {}

    public static void writeBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
