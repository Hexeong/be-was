package handler.adapter;

import model.http.HttpRequest;
import model.http.HttpResponse;
import resolver.view.ModelAndView;

public interface HandlerAdapter {
    boolean canAdapt(Object handler);
    ModelAndView handle(HttpRequest req, HttpResponse res, Object handler);
}
