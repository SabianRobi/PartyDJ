package partydj.backend.rest.domain.error;

public abstract class RequiredFieldException extends RuntimeException {
    protected RequiredFieldException(final String message) {
        super(message);
    }
}
