package routing;

import handler.HandlerMethod;

import java.util.HashMap;
import java.util.Map;

public class HandlerMapping {
    private Map<RouteKey, HandlerMethod> mapping;

    public HandlerMapping() {
        mapping = new HashMap<>();
    }

    public void addMapping(RouteKey key, HandlerMethod handler) {
        mapping.put(key, handler);
    }

    public HandlerMethod getHandler(RouteKey key) {
        return mapping.get(key);
    }
}
