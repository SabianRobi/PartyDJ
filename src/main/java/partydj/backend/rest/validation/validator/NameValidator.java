package partydj.backend.rest.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import partydj.backend.rest.validation.constraint.Name;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameValidator implements ConstraintValidator<Name, String> {
    @Override
    public boolean isValid(final String name, final ConstraintValidatorContext context) {
        String regex = "^[a-zA-Z0-9-_]+$";
        final Pattern p = Pattern.compile(regex);
        final Matcher m = p.matcher(name);

        return m.matches();
    }
}
