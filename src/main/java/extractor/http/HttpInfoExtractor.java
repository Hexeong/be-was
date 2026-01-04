package extractor.http;

public interface HttpInfoExtractor<T> {
    T extract(String target);
}
