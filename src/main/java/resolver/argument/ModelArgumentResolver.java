package resolver.argument;

import model.Model;
import model.http.HttpRequest;

import java.lang.reflect.Parameter;

public class ModelArgumentResolver implements ArgumentResolver{
    @Override
    public boolean supports(Parameter parameter) {
        return parameter.getType().equals(Model.class);
    }

    @Override
    public Object resolve(Parameter parameter, HttpRequest request) {
        return new Model();
    }
}
