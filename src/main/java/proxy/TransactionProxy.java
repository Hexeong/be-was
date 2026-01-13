package proxy;

import annotation.Transactional;
import db.TransactionManager;
import handler.HandlerMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

public class TransactionProxy extends HandlerMethod {
    private static final Logger log = LoggerFactory.getLogger(TransactionProxy.class);

    public TransactionProxy(HandlerMethod handlerMethod) {
        super(handlerMethod.getHandlerInstance(), handlerMethod.getHandlerMethod());
    }

    /**
     * 해당 프록시 객체를 사용할 수 있는 handler인지 검사하여 true/false를 반환합니다.
     * @param handler
     * @return
     */
    public static boolean support(Object handler) {
        if (handler instanceof HandlerMethod hm) {
            return hm.getHandlerMethod().isAnnotationPresent(Transactional.class);
        }
        return false;
    }

    /**
     * 해당 프록시 객체를 실행하여 트랜잭션을 관리하도록 동작하고, handler를 invoke합니다.
     * @param args
     * @return
     * @throws Exception
     */
    @Override
    public Object invoke(Object[] args) throws Exception {
        String methodName = getHandlerMethod().getName();
        try {
            log.debug("Transaction Start: method={}", methodName);
            TransactionManager.startTransaction();

            Object result = super.invoke(args);

            log.debug("Transaction Commit: method={}", methodName);
            TransactionManager.commit();

            return result;

        } catch (InvocationTargetException e) {
            log.debug("Transaction Rollback: method={}", methodName);
            TransactionManager.rollback();
            throw (Exception) e.getTargetException(); // Reflection 예외 껍질 벗기기
        } catch (Exception e) {
            TransactionManager.rollback();
            throw e;
        }
    }
}