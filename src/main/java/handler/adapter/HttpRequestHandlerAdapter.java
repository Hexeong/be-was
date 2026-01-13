package handler.adapter;

import handler.HandlerMethod;
import handler.impl.DynamicHttpHandler;
import model.http.HttpRequest;
import model.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resolver.argument.ArgumentResolver;
import resolver.view.ModelAndView;
import webserver.ApplicationContext;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class HttpRequestHandlerAdapter implements HandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestHandlerAdapter.class);

    private final ApplicationContext context;

    public HttpRequestHandlerAdapter(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public boolean canAdapt(Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            return hm.getHandlerInstance() instanceof DynamicHttpHandler;
        }
        return false;
    }

    @Override
    public ModelAndView handle(HttpRequest req, HttpResponse res, Object handler) {
        HandlerMethod hm = (HandlerMethod) handler;
        Object instance = hm.getHandlerInstance();
        Method method = hm.getHandlerMethod();

        // 1. 메서드의 파라미터 목록을 가져옴
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

        // 2. 파라미터 타입을 보고 알맞은 객체를 args 배열에 할당
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> paramType = parameter.getType();

            // 2-1. HttpRequest, HttpResponse 처리 (우선 순위)
            if (paramType == HttpRequest.class) {
                args[i] = req;
            } else if (paramType == HttpResponse.class) {
                args[i] = res;
            }
            // 2-2. 그 외 파라미터는 ArgumentResolver에게 위임
            else {
                args[i] = resolveArgument(parameter, req);
            }
        }

        try {
            // 3. 리플렉션으로 메서드 실행 (인자값 주입)
            return (ModelAndView) hm.invoke(args);
        } catch (Exception e) {
            throw new RuntimeException("Handler execution failed", e);
        }
    }

    /**
     * 등록된 Resolver들을 순회하며 해당 파라미터를 처리할 수 있는 녀석을 찾는다.
     */
    private Object resolveArgument(Parameter parameter, HttpRequest req) {
        for (ArgumentResolver resolver : context.getArgumentResolverList()) {
            if (resolver.supports(parameter)) {
                return resolver.resolve(parameter, req);
            }
        }
        return null; // 처리할 수 있는 리졸버가 없으면 null 반환
    }
}
