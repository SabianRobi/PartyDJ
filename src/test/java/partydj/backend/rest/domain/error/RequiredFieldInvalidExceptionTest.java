package partydj.backend.rest.domain.error;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RequiredFieldInvalidExceptionTest {

    @Test
    void shouldCreateRequiredFieldInvalidException() {
        final String message = "message";

        final RequiredFieldInvalidException exception = new RequiredFieldInvalidException(message);

        assertThat(exception.getMessage()).isEqualTo(message);
    }
}
