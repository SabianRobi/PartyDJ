package partydj.backend.rest.validation;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import partydj.backend.rest.domain.User;
import partydj.backend.rest.domain.error.RequiredFieldInvalidException;
import partydj.backend.rest.domain.error.RequiredFieldMissingException;
import partydj.backend.rest.domain.request.SaveUserRequest;
import partydj.backend.rest.domain.request.UpdateUserRequest;
import partydj.backend.rest.service.UserService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static partydj.backend.rest.config.UserConfig.USERNAME_MIN_LENGTH;

@Service
public class UserValidator {
    @Autowired
    private UserService userService;

    public void validateOnPost(final SaveUserRequest user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new RequiredFieldMissingException("Email cannot be empty.");
        }
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new RequiredFieldMissingException("Username cannot be empty.");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new RequiredFieldMissingException("Password cannot be empty.");
        }

        VerifyUsernameMinLength(user.getUsername());
        VerifyUsernameAlreadyExists(user.getUsername());
        VerifyEmailFormat(user.getEmail());
        VerifyEmailAlreadyExists(user.getEmail());
    }

    public void validateOnGet(final User user) {
        VerifyUserNotNull(user);
    }

    public void validateOnPatch(final UpdateUserRequest newUserInfos, final User toBeUpdatedUser, final User loggedInUser) {
        VerifyUserNotNull(toBeUpdatedUser);
        VerifySameUser(toBeUpdatedUser, loggedInUser);

        if (newUserInfos.getUsername() != null) {
            VerifyUsernameMinLength(newUserInfos.getUsername());
            VerifyUsernameAlreadyExists(newUserInfos.getUsername(), loggedInUser);
        }
        if (newUserInfos.getEmail() != null) {
            VerifyEmailFormat(newUserInfos.getEmail());
            VerifyEmailAlreadyExists(newUserInfos.getEmail(), loggedInUser);
        }

        if (newUserInfos.getPassword() != null && newUserInfos.getPassword().isBlank()) {
            throw new RequiredFieldMissingException("Password cannot be empty.");
        }
    }

    public void validateOnDelete(final User toBeDeletedUser, final User loggedInUser) {
        VerifyUserNotNull(toBeDeletedUser);
        VerifySameUser(toBeDeletedUser, loggedInUser);

        if (toBeDeletedUser.getPartyRole() != null) {
            throw new IllegalStateException("You can't delete your profile, leave the party first.");
        }
    }

    // Helper verifiers
    private void VerifyEmailFormat(final String email) {
        final String regex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
        final Pattern p = Pattern.compile(regex);
        final Matcher m = p.matcher(email);

        if (!m.matches()) {
            throw new RequiredFieldInvalidException("Invalid email format.");
        }
    }

    private void VerifyUsernameMinLength(final String username) {
        if (username.trim().length() < USERNAME_MIN_LENGTH) {
            throw new RequiredFieldInvalidException("Username must be at least " + USERNAME_MIN_LENGTH + " characters long.");
        }
    }

    private void VerifyEmailAlreadyExists(final String email, final User loggedInUser) {
        User user = userService.findByEmail(email);
        if (user != null && user.getId() != loggedInUser.getId()) {
            throw new IllegalStateException("A user with this email already exists.");
        }
    }

    private void VerifyEmailAlreadyExists(final String email) {
        if (userService.existsByEmail(email)) {
            throw new IllegalStateException("A user with this email already exists.");
        }
    }

    private void VerifyUsernameAlreadyExists(final String username) {
        if (userService.existsByUsername(username)) {
            throw new IllegalStateException("A user with this username already exists.");
        }
    }

    private void VerifyUsernameAlreadyExists(final String username, final User toBeUpdatedUser) {
        User user = userService.findByUsername(username);
        if (user != null && user.getId() != toBeUpdatedUser.getId()) {
            throw new IllegalStateException("A user with this username already exists.");
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
