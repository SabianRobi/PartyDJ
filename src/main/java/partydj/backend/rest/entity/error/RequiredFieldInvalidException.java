package partydj.backend.rest.entity.error;

public class RequiredFieldInvalidException extends RequiredFieldException {
    public RequiredFieldInvalidException(final String message) {
        super(message);
    }
}
