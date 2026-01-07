package business;

import model.http.HttpRequest;
import model.http.HttpResponse;
import resolver.view.ModelAndView;

@FunctionalInterface
public interface BusinessHandler {
    ModelAndView execute(HttpRequest req, HttpResponse res);
}
