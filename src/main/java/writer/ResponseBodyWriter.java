package writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.handler.request.RequestHandler;

import java.io.DataOutputStream;
import java.io.IOException;

public class ResponseBodyWriter {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private static volatile ResponseBodyWriter instance = null;

    private ResponseBodyWriter() {}

    public static ResponseBodyWriter getInstance() {
        if (instance == null) {
            synchronized (ResponseBodyWriter.class) {
                if (instance == null) {
                    instance = new ResponseBodyWriter();
                }
            }
        }
        return instance;
    }

    public void writeBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
