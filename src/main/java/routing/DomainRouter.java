package routing;

import business.BusinessHandler;
import model.http.HttpRequest;

public interface DomainRouter {
    BusinessHandler getHandler(HttpRequest req);
}
