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

    /**
     * Tranfer-Encoding, Content-Length 헤더의 존재 유무에 따라 Body 파싱 로직을 진행합니다. 파싱 이후 전체를 UTF-8로 인코딩하여
     * String 타입으로 반환합니다.
     * @param in
     * @param headers
     * @return String 타입의 body 값
     * @throws IOException
     */
    public static String parse(InputStream in, Map<String, String> headers) throws IOException {
        // [RFC 9112 Section 6.1] Transfer-Encoding 헤더가 있으면 Content-Length는 무조건 무시해야 함.
        //                        만약 둘 다 있는데 Transfer-Encoding이 chunked가 아니라면 에러를 뱉거나 연결을 닫는 것이 원칙
        if (headers.containsKey(TRANSFER_ENCODING_KEY)) {
            if (headers.get(TRANSFER_ENCODING_KEY).equals(VALID_TRANSFER_ENCODING_VALUE)) {
                return parseChunckedBody(in, headers);
            } else
                throw new CustomException(ErrorCode.NOT_IMPLEMENTED);
        }

        if (headers.containsKey(CONTENT_LENGTH_KEY)) {
            return parseFixedLengthBody(in, headers);
        }

        // [RFC 9112 Section 6.3]
        // 요청(Request)에서 Content-Length도 없고 Transfer-Encoding도 없으면 Body 길이는 0이다.
        return "";
    }

    private static String parseChunckedBody(InputStream in, Map<String, String> header) throws IOException {
        ByteArrayOutputStream allBytes = new ByteArrayOutputStream();
        int chunkSize = 0;

        // 청크 루프 시작
        while ((chunkSize = readParsedByHex(in)) != 0) {
            byte[] buffer = new byte[chunkSize]; // 이번 청크 크기만큼 읽을 임시 버퍼
            int totalRead = 0;

            while (totalRead < chunkSize) { // 정확히 chunkSize만큼 읽기 (TCP 파편화 대비)
                int readCount = in.read(buffer, totalRead, chunkSize - totalRead);
                if (readCount == -1) break;
                totalRead += readCount;
            }

            // 읽은 바이트를 String으로 바꾸지 않고, 바이트 저장소에 그대로 작성. (한글 파싱 오류 제거)
            allBytes.write(buffer);

            in.read(); in.read(); // \r\n 스킵
        }

        // 모든 청크를 다 모은 후, 마지막에 딱 한 번 UTF-8로 인코딩.
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
            int contentLength = Integer.parseInt(headers.get(CONTENT_LENGTH_KEY));
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
