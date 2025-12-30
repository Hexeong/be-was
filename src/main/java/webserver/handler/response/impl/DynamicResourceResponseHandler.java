package webserver.handler.response.impl;

import model.http.TotalHttpMessage;
import webserver.handler.response.ResponseHandler;

import java.io.OutputStream;

public class DynamicResourceResponseHandler implements ResponseHandler {

    public DynamicResourceResponseHandler() {}

    public void sendResponse(OutputStream out, TotalHttpMessage message) {
        //TODO:: 동적 요청 처리, 요청 경로에 따른 적절한 handler로 route하도록 Router 클래스 구현 필요
    }
}
