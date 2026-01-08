package handler;

import interceptor.Interceptor;
import model.http.HttpRequest;
import model.http.HttpResponse;

import java.util.List;

public class HandlerExecutionChain {
    private List<Interceptor> interceptorList;
    private Object handler;

    public HandlerExecutionChain(List<Interceptor> interceptorList, Object handler) {
        this.interceptorList = interceptorList;
        this.handler = handler;
    }

    public boolean applyPreHandle(HttpRequest req, HttpResponse res) {
        for (int i = 0; i < interceptorList.size(); i++) {
            if (interceptorList.get(i).intercept(req, res)) { // intercept
                interceptorList.get(i).triggerAfterCompletion(); // 후속 처리
                return false;
            }
        }
        return true;
    }

    public Object getHandler() {
        return this.handler;
    }

    public boolean applyPostHandle(HttpRequest req, HttpResponse res) {
        for (int i = interceptorList.size() - 1; i > -1; i--) {
            if (interceptorList.get(i).intercept(req, res)) { // intercept
                interceptorList.get(i).triggerAfterCompletion(); // 후속 처리
                return false;
            }
        }
        return true;
    }
}
