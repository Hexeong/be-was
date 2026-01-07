package webserver;

import webserver.handler.RequestHandler;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RequestExecutor {
    private static final ExecutorService executorService = Executors.newFixedThreadPool(30);

    RequestExecutor() {}

    public static void submit(Socket connection) {
        executorService.submit(new RequestHandler(connection));
    }
}
