package handler;

import exception.CustomException;
import exception.ErrorCode;
import model.http.sub.RequestMethod;

import java.util.HashMap;
import java.util.Map;

public class HandlerMapping {
    private Map<String, Map<RequestMethod, HandlerMethod>> mapping;

    public HandlerMapping() {
        mapping = new HashMap<>();
    }

    public void addMapping(RouteKey key, HandlerMethod handler) {
        Map<RequestMethod, HandlerMethod> methodMap = mapping.computeIfAbsent(key.urlPath(), k -> new HashMap<>());
        methodMap.put(key.method(), handler);
    }

    public HandlerMethod getHandler(RouteKey key) {
        Map<RequestMethod, HandlerMethod> methodMap = mapping.getOrDefault(key.urlPath(), null);

        if (methodMap == null)
            return null;

        HandlerMethod handler = methodMap.getOrDefault(key.method(), null);

        if (handler == null)
            throw new CustomException(ErrorCode.METHOD_NOT_ALLOWED);

        return handler;
    }
}
