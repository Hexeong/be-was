package util.parser;

import exception.CustomException;
import exception.ErrorCode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class HttpBodyParser {

    private HttpBodyParser() {}

    private static final char CR = '\r';
    private static final char LF = '\n';
    private static final String TRANSFER_ENCODING_KEY = "transfer-encoding";
    private static final String VALID_TRANSFER_ENCODING_VALUE = "chunked";
    private static final String CONTENT_LENGTH_KEY = "content-length";
    private static final String UTF_8 = "UTF-8";
    private static final String DEFAULT_CHARSET = "ISO-8859-1";
    private static final int MAX_BODY_SIZE = 10 * 1024 * 1024;

    /**
     * Tranfer-Encoding, Content-Length 헤더의 존재 유무에 따라 Body 파싱 로직을 진행합니다. 파싱 이후 전체를 UTF-8로 인코딩하여
     * String 타입으로 반환합니다.
     * @param in
     * @param headers
     * @return String 타입의 body 값
     * @throws IOException
     */
    public static String parse(InputStream in, Map<String, String> headers) throws IOException {
        if (headers.containsKey(TRANSFER_ENCODING_KEY)) {
            if (headers.get(TRANSFER_ENCODING_KEY).equals(VALID_TRANSFER_ENCODING_VALUE)) {
                return parseChunkedBody(in, headers);
            } else
                throw new CustomException(ErrorCode.NOT_IMPLEMENTED);
        }

        if (headers.containsKey(CONTENT_LENGTH_KEY)) {
            return parseFixedLengthBody(in, headers);
        }

        return "";
    }

    private static String parseChunkedBody(InputStream in, Map<String, String> header) throws IOException {
        ByteArrayOutputStream allBytes = new ByteArrayOutputStream();
        int chunkSize = 0;
        int totalBytesRead = 0;

        while ((chunkSize = readParsedByHex(in)) != 0) {
            totalBytesRead += chunkSize;
            if (totalBytesRead > MAX_BODY_SIZE) {
                throw new CustomException(ErrorCode.PAYLOAD_TOO_LARGE);
            }

            byte[] buffer = new byte[chunkSize];
            int totalRead = 0;

            while (totalRead < chunkSize) {
                int readCount = in.read(buffer, totalRead, chunkSize - totalRead);
                if (readCount == -1) break;
                totalRead += readCount;
            }

            allBytes.write(buffer);
            in.read(); in.read(); // \r\n 스킵
        }

        return new String(allBytes.toByteArray(), UTF_8);
    }

    private static int readParsedByHex(InputStream in) throws IOException {
        StringBuilder hexLine = new StringBuilder();
        int data;

        while ((data = in.read()) != -1) {
            char c = (char) data;
            if (c == CR) {
                in.read();
                break;
            }
            if (c == ';') {
                while ((data = in.read()) != -1 && (char)data != LF);
                break;
            }
            hexLine.append(c);
        }

        if (hexLine.isEmpty()) return 0;

        try {
            return Integer.parseInt(hexLine.toString().trim(), 16);
        } catch (NumberFormatException e) {
            throw new IOException("Malformatted chunk size");
        }
    }

    private static String parseFixedLengthBody(InputStream inputStream, Map<String, String> headers) throws IOException {
        try {
            long contentLengthLong = Long.parseLong(headers.get(CONTENT_LENGTH_KEY));

            if (contentLengthLong > MAX_BODY_SIZE) {
                throw new CustomException(ErrorCode.PAYLOAD_TOO_LARGE);
            }

            int contentLength = (int) contentLengthLong;

            byte[] bodyBytes = new byte[contentLength];

            int offset = 0;
            while (offset < contentLength) {
                int read = inputStream.read(bodyBytes, offset, contentLength - offset);
                if (read == -1) throw new IOException("Unexpected End of Stream");
                offset += read;
            }

            return new String(bodyBytes, DEFAULT_CHARSET);
        } catch (NumberFormatException e) {
            throw new IOException("Invalid Content-Length format", e);
        }
    }
}
