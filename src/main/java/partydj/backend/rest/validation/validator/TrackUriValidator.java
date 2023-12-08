package partydj.backend.rest.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import partydj.backend.rest.validation.constraint.TrackUri;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TrackUriValidator implements ConstraintValidator<TrackUri, String> {
    @Override
    public boolean isValid(final String uri, final ConstraintValidatorContext context) {
        if (uri == null || uri.isEmpty()) {
            return false;
        }

        // Spotify track
        String regex = "^spotify:track:[a-zA-Z0-9]+$";
        final Pattern p = Pattern.compile(regex);
        final Matcher m = p.matcher(uri);

        return m.matches();
    }
}
