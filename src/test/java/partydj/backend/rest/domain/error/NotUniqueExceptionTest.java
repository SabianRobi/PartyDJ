package partydj.backend.rest.domain.error;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NotUniqueExceptionTest {

    @Test
    void shouldCreateNotUniqueException() {
        final String key = "key";
        final String message = "message";

        final NotUniqueException exception = new NotUniqueException(key, message);

        assertThat(exception.getKey()).isEqualTo(key);
        assertThat(exception.getMessage()).isEqualTo(message);
    }
}
