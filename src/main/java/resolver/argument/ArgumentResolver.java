package resolver.argument;

import model.http.HttpRequest;
import java.lang.reflect.Parameter;

public interface ArgumentResolver {
    boolean supports(Parameter parameter);
    Object resolve(Parameter parameter, HttpRequest request);
}