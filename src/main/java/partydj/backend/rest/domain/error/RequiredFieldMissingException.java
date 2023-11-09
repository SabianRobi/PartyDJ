package partydj.backend.rest.domain.error;

public class RequiredFieldMissingException extends RuntimeException {
    public RequiredFieldMissingException(final String message) {
        super(message);
    }
}
