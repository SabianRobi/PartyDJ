package partydj.backend.rest.domain.error;

public class ThirdPartyApiException extends RuntimeException {
    public ThirdPartyApiException(final String message) {
        super(message);
    }
}
