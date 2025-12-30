package writer;

import model.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import writer.file.StaticResourceType;

import java.io.DataOutputStream;
import java.io.IOException;

public class ResponseHeaderWriter {
    private static final Logger log = LoggerFactory.getLogger(ResponseHeaderWriter.class);

    private static volatile ResponseHeaderWriter instance = null;

    private ResponseHeaderWriter() {}

    public static ResponseHeaderWriter getInstance() {
        if (instance == null) {
            synchronized (ResponseHeaderWriter.class) {
                if (instance == null) {
                    instance = new ResponseHeaderWriter();
                }
            }
        }
        return instance;
    }

    public void writeHeader(DataOutputStream dos, StaticResourceType staticResourceType,
                            int lengthOfBodyContent, HttpStatus status) {
        try {
            dos.writeBytes("HTTP/1.1 " + status.getCode() + " " + status.getMessage() + " \r\n");
            dos.writeBytes("Content-Type: " + staticResourceType.getContentType() + "\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
