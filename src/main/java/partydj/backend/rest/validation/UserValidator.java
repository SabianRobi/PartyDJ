package partydj.backend.rest.validation;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import partydj.backend.rest.domain.User;

import java.util.Objects;

@Component
public class UserValidator {

    public void verifySameUser(final User user, final String toBeDeletedUsername) {
        if (!Objects.equals(user.getUsername(), toBeDeletedUsername)) {
            throw new AccessDeniedException("You can not make changes to other user profiles.");
        }
    }

    public void verifyNotNull(final User user) {
        if (user == null) {
            throw new EntityNotFoundException("User does not exists.");
        }
    }

}
