package webserver;

import annotation.Router;
import annotation.RequestMapping;
import exception.CustomException;
import exception.ErrorCode;
import handler.HandlerMethod;
import handler.adapter.HandlerAdapter;
import handler.HandlerExecutionChain;
import interceptor.Interceptor;
import model.http.HttpRequest;
import model.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resolver.argument.ArgumentResolver;
import routing.HandlerMapping;
import routing.RouteKey;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Predicate;

public class ApplicationContext {
    private static final Logger log = LoggerFactory.getLogger(ApplicationContext.class);

    private final Dispatcher dispatcher;

    private final List<Object> handlerList;
    private final List<HandlerAdapter> adapterList;
    private final List<Interceptor> interceptorList;
    private final List<ArgumentResolver> argumentResolverList;

    private final HandlerMapping mapping;

    private static final String HANDLER_PACKAGE_PATH = "handler.impl";
    private static final String HANDLER_ADAPTER_PACKAGE_PATH = "handler.adapter";
    private static final String INTERCEPTOR_PACKAGE_PATH = "interceptor";
    private static final String ARGUMENT_RESOLVER_PACKAGE_PATH = "resolver.argument";

    public ApplicationContext() {
        this.dispatcher = new Dispatcher(this);
        this.mapping = new HandlerMapping(); // HandlerMapping 인스턴스 초기화

        // 1. 컴포넌트 스캔 (핸들러, 어댑터, 인터셉터)
        // 핸들러: @Controller 어노테이션이 붙은 클래스 스캔
        this.handlerList = scanPackage(HANDLER_PACKAGE_PATH,
                clazz -> clazz.isAnnotationPresent(Router.class));

        // 어댑터: HandlerAdapter 인터페이스를 구현한 클래스 스캔
        this.adapterList = scanPackage(HANDLER_ADAPTER_PACKAGE_PATH,
                clazz -> HandlerAdapter.class.isAssignableFrom(clazz),
                this);

        // 인터셉터: Interceptor 인터페이스를 구현한 클래스 스캔
        this.interceptorList = scanPackage(INTERCEPTOR_PACKAGE_PATH,
                clazz -> Interceptor.class.isAssignableFrom(clazz));

        // 알규먼트 리졸버: ArgumentResolver 인터페이스를 구현한 클래스 스캔
        this.argumentResolverList = scanPackage(ARGUMENT_RESOLVER_PACKAGE_PATH,
                clazz -> ArgumentResolver.class.isAssignableFrom(clazz));

        // 2. 스캔된 핸들러를 기반으로 매핑 정보 생성
        initMapping();
    }

    private void initMapping() {
        for (Object handler : this.handlerList) {
            Method[] methods = handler.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping rm = method.getAnnotation(RequestMapping.class);

                    for (String pathUrl : rm.path()) {

                        RouteKey key = new RouteKey(rm.method(), pathUrl);
                        HandlerMethod handlerMethod = new HandlerMethod(handler, method);

                        mapping.addMapping(key, handlerMethod);
                    }
                }
            }
        }
    }

    /**
     * 패키지 스캔 공통 로직
     * Predicate를 사용하여 '어노테이션 체크'와 '인터페이스 구현 체크'를 모두 지원
     */
    private <T> List<T> scanPackage(String packageName, Predicate<Class<?>> filter, Object... constructorArgs) {
        List<T> instances = new ArrayList<>();
        String path = packageName.replace('.', '/');

        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> resources = classLoader.getResources(path);
            List<File> dirs = new ArrayList<>();

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                dirs.add(new File(resource.getFile()));
            }

            for (File directory : dirs) {
                instances.addAll(findClasses(directory, packageName, filter, constructorArgs));
            }

        } catch (Exception e) {
            throw new CustomException(ErrorCode.CLASS_SCAN_ERROR, "Failed to scan package: " + packageName);
        }

        return instances;
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> findClasses(File directory, String packageName, Predicate<Class<?>> filter, Object... constructorArgs)
            throws Exception {

        List<T> instances = new ArrayList<>();
        if (!directory.exists()) {
            return instances;
        }

        File[] files = directory.listFiles();
        if (files == null) return instances;

        for (File file : files) {
            if (file.isDirectory()) {
                // 재귀 호출
                instances.addAll(findClasses(file, packageName + "." + file.getName(), filter, constructorArgs));
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                Class<?> clazz = Class.forName(className);

                // 전달받은 필터(어노테이션 존재 여부 or 인터페이스 구현 여부) 체크
                // 인터페이스 자체나 추상 클래스는 인스턴스화 제외
                if (filter.test(clazz)
                        && !clazz.isInterface()
                        && !Modifier.isAbstract(clazz.getModifiers())) {

                    T instance;
                    if (constructorArgs.length > 0 && constructorArgs[0] instanceof ApplicationContext) {
                        // ApplicationContext를 파라미터로 받는 생성자를 찾음 (HandlerAdapter 등)
                        try {
                            Constructor<?> constructor = clazz.getDeclaredConstructor(ApplicationContext.class);
                            instance = (T) constructor.newInstance(constructorArgs[0]);
                        } catch (NoSuchMethodException e) {
                            // 만약 Context를 받는 생성자가 없다면? -> 기본 생성자 시도 (유연성 확보)
                            instance = (T) clazz.getDeclaredConstructor().newInstance();
                        }
                    } else {
                        // 인자가 없으면 기본 생성자 사용
                        instance = (T) clazz.getDeclaredConstructor().newInstance();
                    }
                    instances.add(instance);
                }
            }
        }
        return instances;
    }

    public void doDispatch(HttpRequest req, HttpResponse res) throws IOException {
        dispatcher.doDispatch(req, res);
    }

    public HandlerExecutionChain getHandler(HttpRequest req) {
        // HandlerMapping에서 요청에 맞는 HandlerMethod를 찾아옴
        RouteKey routeKey = new RouteKey(req.line().getMethod(), req.line().getPathUrl());
        Object handler = mapping.getHandler(routeKey); // HandlerMethod가 Object 타입으로 반환됨

        // 핸들러가 없으면 404 예외 처리 등이 필요할 수 있음
        if (handler == null) {
            return null;
        }

        // interceptor 로직 (모든 인터셉터 포함 예시)
        // 실제로는 URL 패턴 매칭 등을 통해 필터링 해야 함
        List<Interceptor> possibleInterceptorList = new ArrayList<>(this.interceptorList);

        return new HandlerExecutionChain(possibleInterceptorList, handler);
    }

    public HandlerAdapter getHandlerAdapter(Object handler) {
        for (HandlerAdapter ha : adapterList) {
            if (ha.canAdapt(handler)) {
                return ha;
            }
        }
        throw new CustomException(ErrorCode.CANNOT_ADAPT);
    }

    public List<ArgumentResolver> getArgumentResolverList() {
        return this.argumentResolverList;
    }
}