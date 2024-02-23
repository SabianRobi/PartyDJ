package partydj.backend.rest.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import partydj.backend.rest.entity.request.UpdateUserPasswordRequest;
import partydj.backend.rest.validation.constraint.NewPasswordsMatch;

public class NewPasswordsMatchValidator implements ConstraintValidator<NewPasswordsMatch, UpdateUserPasswordRequest> {


    @Override
    public boolean isValid(final UpdateUserPasswordRequest request, final ConstraintValidatorContext context) {
        if (request == null
                || request.getPassword().isBlank()
                || request.getConfirmPassword().isBlank()) {
            return false;
        }

        return request.getPassword().equals(request.getConfirmPassword());
    }
}
