package partydj.backend.rest.entity.error;

public abstract class RequiredFieldException extends RuntimeException {
    protected RequiredFieldException(final String message) {
        super(message);
    }
}
