package webserver.handler;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;

import business.BusinessHandler;
import model.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import model.http.HttpRequest;
import parser.http.HttpParserFacade;
import resolver.view.ModelAndView;
import routing.TotalRouteMapping;

public class RawRequestHandler implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(RawRequestHandler.class);

    private Socket connection;

    public RawRequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
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
                    HttpResponse res = new HttpResponse();

                    log.debug(req.toString());

                    // Handler를 가져온다. 처리 결과가 ViewName 또는 HttpResponse
                    BusinessHandler handler = TotalRouteMapping.route(req);

                    if (handler == null) {
                        ResourceResponseHandler.handle(req, res);
                        res.sendResponse(out);
                        continue;
                    }

                    ModelAndView mv = handler.execute(req, res);

                    // TODO:: forward에 대한 처리를 할경우, 다시 route부터 실행해야 한다.

                    mv.resolve(req, res);

                    res.sendResponse(out);
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
