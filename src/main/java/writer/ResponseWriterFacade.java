package writer;

import model.http.HttpStatus;
import writer.file.StaticResourceType;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class ResponseWriterFacade {
    private static final String REDIRECT_HEADER_KEY = "Location";
    private static final String CONTENT_TYPE_KEY = "Content-Type";
    private static final String COOKIE_HEADER_KEY = "Set-Cookie";
    private static final String COOKIE_HEADER_PREFIX = "sid=";
    private static final String COOKIE_HEADER_SUFFIX = "; Path=/";

    public static void sendRedirect(OutputStream out, String redirectPath) {
        DataOutputStream dos = new DataOutputStream(out);
        ResponseHeaderWriter.writeHeader(
                dos,
                Map.of(REDIRECT_HEADER_KEY, redirectPath),
                0,
                HttpStatus.FOUND);

        ResponseBodyWriter.writeBody(dos, new byte[0]);
    }

    public static void sendRedirectWithSessionCookie(OutputStream out, String redirectPath) {
        DataOutputStream dos = new DataOutputStream(out);
        int randomSid = ThreadLocalRandom.current().nextInt(100000, 1000000);
        ResponseHeaderWriter.writeHeader(
                dos,
                Map.of(REDIRECT_HEADER_KEY, redirectPath,
                        COOKIE_HEADER_KEY, COOKIE_HEADER_PREFIX + randomSid + COOKIE_HEADER_SUFFIX),
                0,
                HttpStatus.FOUND);

        ResponseBodyWriter.writeBody(dos, new byte[0]);
    }

    public static void send200FileResponse(OutputStream out, String contentType, byte[] body) {
        DataOutputStream dos = new DataOutputStream(out);
        ResponseHeaderWriter.writeHeader(
                dos,
                Map.of(CONTENT_TYPE_KEY, contentType),
                body.length,
                HttpStatus.OK);
        ResponseBodyWriter.writeBody(dos, body);
    }

    public static void send404NotFoundResponse(OutputStream out) {
        DataOutputStream dos = new DataOutputStream(out);
        ResponseHeaderWriter.writeHeader(
                dos,
                Map.of(CONTENT_TYPE_KEY, StaticResourceType.HTML.getContentType()),
                0,
                HttpStatus.NOT_FOUND);
        ResponseBodyWriter.writeBody(dos, new byte[0]);
    }
}
