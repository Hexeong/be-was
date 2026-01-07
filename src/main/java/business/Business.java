package business;

import model.http.TotalHttpMessage;

import java.io.OutputStream;

@FunctionalInterface
public interface Business {
    void execute(OutputStream out, TotalHttpMessage msg);
}
