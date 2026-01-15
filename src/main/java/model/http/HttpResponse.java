package model.http;

import model.http.sub.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public final class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    private HttpVersion version;
    private HttpStatus status;
    private Map<String, List<String>> headers;
    private byte[] body;
    private OutputStream out;

    public HttpResponse(
            HttpVersion version,
            HttpStatus status,
            Map<String, List<String>> headers, // 생성자 파라미터 변경
            byte[] body,
            OutputStream out
    ) {
        this.version = version;
        this.status = status;
        this.headers = headers;
        this.body = body;
        this.out = out;
    }

    public HttpResponse(OutputStream out) {
        this(
                HttpVersion.HTTP_1_1,
                HttpStatus.OK,
                new HashMap<>(),
                new byte[0],
                out
        );
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

    public void addHeader(String key, String value) {
        headers.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
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

    private void writeHeader(DataOutputStream dos) {
        try {
            dos.writeBytes(version.getVersion() + " " + status.getCode() + " " + status.getMessage() + " \r\n");

            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                String key = entry.getKey();
                for (String value : entry.getValue()) {
                    dos.writeBytes(key + ": " + value + "\r\n");
                }
            }

            dos.writeBytes("Content-Length: " + body.length + "\r\n");
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

    // ... Getter, equals, hashCode, toString ...

    public HttpVersion getVersion() {
        return version;
    }

    public HttpStatus getStatus() {
        return status;
    }

    // [주의] 이제 Map<String, List<String>>을 반환함
    public Map<String, List<String>> getHeaders() {
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
                Arrays.equals(this.body, that.body); // byte[] 비교는 Arrays.equals 권장
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(version, status, headers);
        result = 31 * result + Arrays.hashCode(body);
        return result;
    }

    @Override
    public String toString() {
        return "HttpResponse[" +
                "version=" + version + ", " +
                "status=" + status + ", " +
                "headers=" + headers + ", " +
                "body=" + Arrays.toString(body) + ']';
    }
}