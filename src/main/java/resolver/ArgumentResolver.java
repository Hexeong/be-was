package resolver;

public interface ArgumentResolver<T> {
    T resolve(String bodyText);
}
