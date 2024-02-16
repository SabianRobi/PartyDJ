package partydj.backend.rest.validation.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import partydj.backend.rest.validation.validator.UUIDValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UUIDValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UUID {
    String message() default "Invalid UUID.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}