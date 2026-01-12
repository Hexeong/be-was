package handler;

import java.lang.reflect.Method;

public class HandlerMethod {
    private Object handlerInstance;
    private Method handlerMethod;

    public HandlerMethod(Object handlerInstance, Method handlerMethod) {
        this.handlerInstance = handlerInstance;
        this.handlerMethod = handlerMethod;
    }

    public Object getHandlerInstance() {
        return handlerInstance;
    }

    public Method getHandlerMethod() {
        return handlerMethod;
    }
}
