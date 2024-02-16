package partydj.backend.rest.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import partydj.backend.rest.validation.constraint.UUID;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UUIDValidator implements ConstraintValidator<UUID, java.util.UUID> {
    @Override
    public boolean isValid(final java.util.UUID uuid, final ConstraintValidatorContext context) {
        if (uuid == null || uuid.toString().isEmpty()) {
            return false;
        }

        // Spotify track
        String regex = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        final Pattern p = Pattern.compile(regex);
        final Matcher m = p.matcher(uuid.toString());

        return m.matches();
    }
}
