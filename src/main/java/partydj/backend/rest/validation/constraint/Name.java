package partydj.backend.rest.validation.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import partydj.backend.rest.validation.validator.NameValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NameValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Name {
    String message() default "Invalid name. Usable characters: English alphabet (a-z, A-Z), dash ( \"-\" ) and underscore ( \"_\" ).";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}