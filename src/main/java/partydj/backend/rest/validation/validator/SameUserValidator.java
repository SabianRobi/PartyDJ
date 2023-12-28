package partydj.backend.rest.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;
import partydj.backend.rest.entity.User;
import partydj.backend.rest.validation.constraint.SameUser;

import java.util.Objects;

@SupportedValidationTarget(ValidationTarget.PARAMETERS)
public class SameUserValidator implements ConstraintValidator<SameUser, Object[]> {
    @Override
    public boolean isValid(final Object[] value, final ConstraintValidatorContext context) {
        if (value[0] == null || value[1] == null) {
            return false;
        }

        if (!(value[0] instanceof User) || !(value[1] instanceof String)) {
            throw new IllegalArgumentException("Illegal method signature, expected User and String parameters.");
        }

        return Objects.equals(((User) value[0]).getUsername(), value[1]);
    }
}
