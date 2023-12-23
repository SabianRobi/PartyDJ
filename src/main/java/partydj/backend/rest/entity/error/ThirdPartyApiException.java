package partydj.backend.rest.entity.error;

public class ThirdPartyApiException extends RuntimeException {
    public ThirdPartyApiException(final String message) {
        super(message);
    }
}
