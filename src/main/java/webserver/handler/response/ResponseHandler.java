package webserver.handler.response;

import model.http.TotalHttpMessage;

import java.io.OutputStream;

public interface ResponseHandler {
    boolean sendResponse(OutputStream out, TotalHttpMessage message);
}
