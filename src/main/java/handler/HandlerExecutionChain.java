package handler;

import exception.CustomException;
import interceptor.Interceptor;
import model.http.HttpRequest;
import model.http.HttpResponse;

import java.util.List;

public class HandlerExecutionChain {
    private final List<Interceptor> interceptorList;
    private final Object handler;

    public HandlerExecutionChain(List<Interceptor> interceptorList, Object handler) {
        this.interceptorList = interceptorList;
        this.handler = handler;
    }

    public boolean applyPreHandle(HttpRequest req, HttpResponse res) throws CustomException {
        for (int i = 0; i < interceptorList.size(); i++) {
            if (!interceptorList.get(i).preHandle(req, res, this.handler)) {
                triggerAfterCompletion(req, res, i);
                return false;
            }
        }
        return true;
    }

    public Object getHandler() {
        return this.handler;
    }

    public boolean applyPostHandle(HttpRequest req, HttpResponse res) throws CustomException {
        for (int i = interceptorList.size() - 1; i > -1; i--) {
            if (!interceptorList.get(i).postHandle(req, res, this.handler)) {
                return false;
            }
        }
        return true;
    }

    private void triggerAfterCompletion(HttpRequest req, HttpResponse res, int interceptorIndex) {
        for (int i = interceptorIndex; i >= 0; i--) {
            Interceptor interceptor = this.interceptorList.get(i);
            interceptor.afterCompletion(req, res, this.handler);
        }
    }
}
