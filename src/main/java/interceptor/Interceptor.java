package interceptor;

import model.http.HttpRequest;
import model.http.HttpResponse;

public interface Interceptor {
    boolean intercept(HttpRequest req, HttpResponse res);
    void triggerAfterCompletion();
}
