package model.http;

import exception.CustomException;
import model.http.sub.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    private HttpVersion version;
    private HttpStatus status;
    private Map<String, String> headers;
    private byte[] body;
    private OutputStream out;

    public HttpResponse(
            HttpVersion version,
            HttpStatus status,
            Map<String, String> headers,
            byte[] body,
            OutputStream out
    ) {
        this.version = version;
        this.status = status;
        this.headers = headers;
        this.body = body;
        this.out = out;
    }

    public HttpResponse(HttpRequest req, OutputStream out) {
        this(
                req.line().getVersion(),
                HttpStatus.OK,
                new HashMap<>(),
                new byte[0],
                out
        );
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void setVersion(HttpVersion version) {
        this.version = version;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void sendResponse() {
        DataOutputStream dos = new DataOutputStream(this.out);
        writeHeader(dos);
        writeBody(dos);
    }

    public void sendExceptionResponse(CustomException e) {
        DataOutputStream dos = new DataOutputStream(this.out);

        this.status = e.getCode().getStatus();
        this.body = (e.getCode().getMessage() + " " + e.getSpecificMessage()).getBytes(StandardCharsets.UTF_8);
        this.headers.put("Content-Type", "text/plain; charset=utf-8");

        writeHeader(dos);
        writeBody(dos);
    }

    private void writeHeader(DataOutputStream dos) {
        try {
            dos.writeBytes( version.getVersion() + " " + status.getCode() + " " + status.getMessage() + " \r\n");
            for (Map.Entry<String, String> header : headers.entrySet()) {
                dos.writeBytes(header.getKey() + ":" + header.getValue() + "\r\n");
            }
            dos.writeBytes("Content-Length:" + body.length + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void writeBody(DataOutputStream dos) {
        try {
            if (body != null)
                dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public HttpVersion getVersion() {
        return version;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public Map<String, String> headers() {
        return headers;
    }

    public byte[] body() {
        return body;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (HttpResponse) obj;
        return Objects.equals(this.version, that.version) &&
                Objects.equals(this.status, that.status) &&
                Objects.equals(this.headers, that.headers) &&
                Objects.equals(this.body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, status, headers, body);
    }

    @Override
    public String toString() {
        return "HttpResponse[" +
                "version=" + version + ", " +
                "status=" + status + ", " +
                "headers=" + headers + ", " +
                "body=" + body + ']';
    }
}
