package partydj.backend.rest.domain.error;

public abstract class RequiredFieldException extends RuntimeException {
    protected RequiredFieldException(String message) {
        super(message);
    }
}
