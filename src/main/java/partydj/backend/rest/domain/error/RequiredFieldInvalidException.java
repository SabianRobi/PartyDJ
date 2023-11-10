package partydj.backend.rest.domain.error;

public class RequiredFieldInvalidException extends RequiredFieldException {
    public RequiredFieldInvalidException(final String message) {
        super(message);
    }
}
