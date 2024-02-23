package partydj.backend.rest.validation.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import partydj.backend.rest.validation.validator.NewPasswordsMatchValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NewPasswordsMatchValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NewPasswordsMatch {
    String message() default "The given new passwords does not match.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}