package partydj.backend.rest.validation.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import partydj.backend.rest.validation.validator.ExistsValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ExistsValidator.class)
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Exists {
    String message() default "Entity does not exists.";

    String type() default "Entity";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}