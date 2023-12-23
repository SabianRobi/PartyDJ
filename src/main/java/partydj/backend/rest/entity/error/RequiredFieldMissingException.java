package partydj.backend.rest.entity.error;

public class RequiredFieldMissingException extends RequiredFieldException {
    public RequiredFieldMissingException(final String message) {
        super(message);
    }
}
