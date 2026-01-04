package webserver.handler.response.impl;

import model.http.TotalHttpMessage;
import routing.TotalRouteMapping;
import webserver.handler.response.ResponseHandler;

import java.io.OutputStream;

public class DynamicResourceResponseHandler implements ResponseHandler {

    public DynamicResourceResponseHandler() {}

    public boolean sendResponse(OutputStream out, TotalHttpMessage message) {
        // TODO:: 들어오는 Content-Type에 따른 Body에 대해 적절한 파싱이 이루어져야 한다.
        return TotalRouteMapping.route(out, message);
    }
}
