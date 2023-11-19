package partydj.backend.rest.domain.error;

public class ThirdPartyAPIError extends RuntimeException {
    public ThirdPartyAPIError(final String message) {
        super(message);
    }
}
