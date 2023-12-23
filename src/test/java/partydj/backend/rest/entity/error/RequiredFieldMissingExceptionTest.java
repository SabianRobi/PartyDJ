package partydj.backend.rest.entity.error;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RequiredFieldMissingExceptionTest {

    @Test
    void shouldCreateRequiredFieldMissingException() {
        final String message = "message";

        final RequiredFieldMissingException exception = new RequiredFieldMissingException(message);

        assertThat(exception.getMessage()).isEqualTo(message);
    }
}
