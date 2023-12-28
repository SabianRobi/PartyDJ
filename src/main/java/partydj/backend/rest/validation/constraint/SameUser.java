package partydj.backend.rest.validation.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import partydj.backend.rest.validation.validator.SameUserValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = SameUserValidator.class)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface SameUser {
    String message() default "You can not make changes to other user profiles.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}