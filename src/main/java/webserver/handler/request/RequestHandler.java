package webserver.handler.request;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import model.http.TotalHttpMessage;
import parser.http.HttpParserFacade;
import webserver.handler.response.RequestResourceType;
import writer.file.StaticResourceType;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            connection.setSoTimeout(5000);

            while(true) {
                try {
                    TotalHttpMessage totalHttpMessage = HttpParserFacade.getInstance().parse(in);

                    logger.debug(totalHttpMessage.toString());

                    // 현재 요청 자원이 정적 파일인지 url을 보고 파악, 만약 dynamic이면 알아서 Enum에 등록된 Handler를 통해 처리
                    RequestResourceType.findByRequestResourceType(StaticResourceType.isStaticResourceByUrl(totalHttpMessage.line().url()))
                            .getHandler().sendResponse(out, totalHttpMessage);
                } catch (SocketTimeoutException e) {
                    logger.error(e.getMessage());
                    break;
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            logger.error(e.getMessage());
        }
    }
}
