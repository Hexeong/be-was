package interceptor;

import exception.CustomException;
import model.http.HttpRequest;
import model.http.HttpResponse;

public interface Interceptor {
    default boolean preHandle(HttpRequest req, HttpResponse res, Object handler) throws CustomException {
        return true; // 통과면 true
    };
    default boolean postHandle(HttpRequest req, HttpResponse res, Object handler) throws CustomException {
        return true;
    };
    default void afterCompletion(HttpRequest req, HttpResponse res, Object handler) {
        // 보통 들어가는 로직은 다음과 같다.
        // 1. ThreadLocal 정리
        // 2. 리소스 반환
        // 3. 실행 시간 로깅
    };
}
