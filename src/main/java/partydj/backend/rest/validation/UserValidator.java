package partydj.backend.rest.validation;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import partydj.backend.rest.domain.User;

@Service
public class UserValidator {
    public void validateOnDelete(final User toBeDeletedUser, final User loggedInUser) {
        VerifyUserNotNull(toBeDeletedUser);
        VerifySameUser(toBeDeletedUser, loggedInUser);

        if (toBeDeletedUser.getPartyRole() != null) {
            throw new IllegalStateException("You can't delete your profile, leave the party first.");
        }
    }

    private void VerifyUserNotNull(final User user) {
        if (user == null) {
            throw new EntityNotFoundException("User does not exists.");
        }
    }

    private void VerifySameUser(final User user1, final User user2) {
        if (user1.getId() != user2.getId()) {
            throw new AccessDeniedException("You don't have permission to make changes to this user's profile.");
        }
    }
}
