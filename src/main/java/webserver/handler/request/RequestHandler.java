package webserver.handler.request;

import java.io.*;
import java.net.Socket;

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

        // TODO:: HTTP/1.1의 경우 Keep-Alive로 인한 요청 유지가 가능. 해당 기능을 이용해 한 번 생성한 연결로 이후 요청도 처리하도록 리팩토링해보기
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

            TotalHttpMessage totalHttpMessage = HttpParserFacade.getInstance().parse(in);

            logger.debug(totalHttpMessage.toString());

            // 현재 요청 자원이 정적 파일인지 url을 보고 파악, 만약 dynamic이면 알아서 Enum에 등록된 Handler를 통해 처리
            RequestResourceType.findByRequestResourceType(StaticResourceType.isStaticResourceByUrl(totalHttpMessage.line().url()))
                    .getHandler().sendResponse(out, totalHttpMessage);
        } catch (IOException | IllegalArgumentException e) {
            logger.error(e.getMessage());
        }
    }
}
