package webserver.handler;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;

import exception.CustomException;
import model.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import model.http.HttpRequest;
import parser.http.HttpParserFacade;
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
                try {
                    // 1. request parsing
                    HttpRequest req = HttpParserFacade.parse(in);
                    HttpResponse res = new HttpResponse(req, out);

                    log.debug(req.toString());

                    try {
                        context.doDispatch(req, res);
                        res.sendResponse();
                    } catch (CustomException e) {
                        res.sendExceptionResponse(e);
                    }

                } catch (SocketTimeoutException e) {
                    log.error(e.getMessage());
                    break;
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            log.error(e.getMessage());
        }
    }
}
