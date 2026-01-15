package webserver;

import handler.adapter.HandlerAdapter;
import handler.HandlerExecutionChain;
import model.http.HttpRequest;
import model.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resolver.view.ModelAndView;
import webserver.handler.ResourceResponseHandler;

public class Dispatcher {
    private static final Logger log = LoggerFactory.getLogger(Dispatcher.class);

    private final ApplicationContext context;

    public Dispatcher(ApplicationContext context) {
        this.context = context;
    }

    public void doDispatch(HttpRequest req, HttpResponse res) throws Exception {
        HandlerExecutionChain mappedHandler = context.getHandler(req);

        if (mappedHandler == null) {
            ResourceResponseHandler.handle(req, res);
            return;
        }

        HandlerAdapter ha = context.getHandlerAdapter(mappedHandler.getHandler());

        if (!mappedHandler.applyPreHandle(req, res)) {
            return;
        }

        ModelAndView mv = ha.handle(req, res, mappedHandler.getHandler());

        // TODO:: forward에 대한 처리를 할경우, 다시 route부터 실행해야 한다.

        if (!mappedHandler.applyPostHandle(req, res)) {
            return;
        }

        mv.resolve(res);
    }
}
