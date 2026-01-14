package webserver.handler;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;

import exception.CustomException;
import model.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import model.http.HttpRequest;
import util.parser.HttpParserFacade;
import webserver.ApplicationContext;

public class RawRequestHandler implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(RawRequestHandler.class);

    private Socket connection;
    private ApplicationContext context;

    public RawRequestHandler(Socket connectionSocket, ApplicationContext context) {
        this.connection = connectionSocket;
        this.context = context;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            connection.setSoTimeout(5000);

            while(true) {
                HttpResponse res = new HttpResponse(out);
                try {
                    HttpRequest req = HttpParserFacade.parse(in);
                    res.setVersion(req.line().getVersion());

                    log.debug(req.toString());

                    context.doDispatch(req, res);
                    res.sendResponse();

                } catch (CustomException e) {
                    ErrorResponseHandler.responseError(res, e);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    break;
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            log.error(e.getMessage());
        }
    }
}
