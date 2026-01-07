package resolver.argument;

public interface ArgumentResolver<T> {
    T resolve(String bodyText);
}
