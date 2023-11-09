package partydj.backend.rest.domain.error;

public class RequiredFieldInvalidException extends RuntimeException {
    public RequiredFieldInvalidException(final String message) {
        super(message);
    }
}
