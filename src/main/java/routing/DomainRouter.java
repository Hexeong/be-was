package routing;

import model.http.TotalHttpMessage;

import java.io.OutputStream;

public interface DomainRouter {
    boolean route(OutputStream out, TotalHttpMessage message);
}
