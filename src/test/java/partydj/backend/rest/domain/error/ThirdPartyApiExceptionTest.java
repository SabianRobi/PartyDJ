package partydj.backend.rest.domain.error;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ThirdPartyApiExceptionTest {

    @Test
    void shouldCreateThirdPartyApiException() {
        final String message = "message";

        final ThirdPartyApiException exception = new ThirdPartyApiException(message);

        assertThat(exception.getMessage()).isEqualTo(message);
    }
}
