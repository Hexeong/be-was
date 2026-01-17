package webserver;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApplicationContextDuplicateMappingTest {
    @Test
    void 애플리케이션_시작시_중복된_요청_매핑이_있으면_실패한다() {
        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> new ApplicationContext("fixture.duplicate"));

        assertThat(error.getMessage())
                .contains("GET /duplicate", "FirstRouter", "SecondRouter");
    }
}
