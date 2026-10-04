package handler;

import exception.CustomException;
import interceptor.Interceptor;
import model.http.HttpRequest;
import model.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class HandlerExecutionChain {
    private static final Logger log = LoggerFactory.getLogger(HandlerExecutionChain.class);

    private final List<Interceptor> interceptorList;
    private final Object handler;
    private int interceptorIndex = -1; // preHandle이 true를 반환한 마지막 Interceptor의 인덱스

    public HandlerExecutionChain(List<Interceptor> interceptorList, Object handler) {
        this.interceptorList = interceptorList;
        this.handler = handler;
    }

    public boolean applyPreHandle(HttpRequest req, HttpResponse res) throws CustomException {
        for (int i = 0; i < interceptorList.size(); i++) {
            if (!interceptorList.get(i).preHandle(req, res, this.handler)) {
                triggerAfterCompletion(req, res, null);
                return false;
            }
            this.interceptorIndex = i;
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

    /**
     * preHandle을 통과한 Interceptor에 대해서만 역순으로 afterCompletion을 호출한다.
     * 하나의 afterCompletion이 실패해도 나머지 Interceptor의 정리 로직은 계속 실행된다.
     * 한 번 호출된 이후에는 다시 호출되어도 아무 동작을 하지 않는다.
     */
    public void triggerAfterCompletion(HttpRequest req, HttpResponse res, Exception ex) {
        for (int i = this.interceptorIndex; i >= 0; i--) {
            Interceptor interceptor = this.interceptorList.get(i);
            try {
                interceptor.afterCompletion(req, res, this.handler, ex);
            } catch (Exception e) {
                log.error("afterCompletion 실행 중 예외 발생: {}", e.getMessage());
            }
        }
        this.interceptorIndex = -1; // 중복 호출 방지
    }
}
