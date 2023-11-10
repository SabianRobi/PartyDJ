package partydj.backend.rest.domain.error;

public class RequiredFieldMissingException extends RequiredFieldException {
    public RequiredFieldMissingException(final String message) {
        super(message);
    }
}
