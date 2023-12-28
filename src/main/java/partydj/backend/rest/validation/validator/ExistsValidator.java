package partydj.backend.rest.validation.validator;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import partydj.backend.rest.validation.constraint.Exists;

public class ExistsValidator implements ConstraintValidator<Exists, Object> {
    private String type;

    public void initialize(final Exists constraintAnnotation) {
        type = constraintAnnotation.type();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        System.err.println("Null check running");
        if (value == null) {
            System.err.println("Result: null");
            throw new EntityNotFoundException(type + " does not exists.");
        }
        System.err.println("Result: not null, value: " + value);

        return true;
    }
}
