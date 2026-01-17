package routing;

import handler.HandlerMapping;
import handler.HandlerMethod;
import handler.RouteKey;
import model.http.sub.RequestMethod;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HandlerMappingTest {
    private final HandlerMapping mapping = new HandlerMapping();
    private final TestHandlers handlers = new TestHandlers();

    @Test
    void 같은_메서드와_경로를_등록하면_기존_핸들러를_유지하고_예외를_던진다() throws NoSuchMethodException {
        RouteKey key = new RouteKey(RequestMethod.GET, "/duplicate");
        HandlerMethod first = handlerMethod("first");
        HandlerMethod second = handlerMethod("second");
        mapping.addMapping(key, first);

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> mapping.addMapping(key, second));

        assertThat(error.getMessage())
                .contains("GET /duplicate", "first", "second");
        assertThat(mapping.getHandler(key)).isSameAs(first);
    }

    @Test
    void 경로가_같아도_메서드가_다르면_각각_등록한다() throws NoSuchMethodException {
        RouteKey getKey = new RouteKey(RequestMethod.GET, "/shared");
        RouteKey postKey = new RouteKey(RequestMethod.POST, "/shared");
        HandlerMethod getHandler = handlerMethod("first");
        HandlerMethod postHandler = handlerMethod("second");

        mapping.addMapping(getKey, getHandler);
        mapping.addMapping(postKey, postHandler);

        assertThat(mapping.getHandler(getKey)).isSameAs(getHandler);
        assertThat(mapping.getHandler(postKey)).isSameAs(postHandler);
    }

    private HandlerMethod handlerMethod(String name) throws NoSuchMethodException {
        return new HandlerMethod(handlers, TestHandlers.class.getDeclaredMethod(name));
    }

    private static class TestHandlers {
        void first() { }
        void second() { }
    }
}
