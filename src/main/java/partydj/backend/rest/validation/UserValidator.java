package partydj.backend.rest.validation;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import partydj.backend.rest.domain.User;
import partydj.backend.rest.service.UserService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static partydj.backend.rest.config.UserConfig.USERNAME_MIN_LENGTH;

@Service
public class UserValidator {
    @Autowired
    private UserService userService;

    public void validateOnGetAndDelete(User user) {
        if (user == null) {
            throw new EntityNotFoundException("User does not exists.");
        }
    }

    public void validateOnPost(User user) {
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalStateException("Email cannot be empty.");
        }
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalStateException("Username cannot be empty.");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalStateException("Password cannot be empty.");
        }

        CheckUsernameMinLength(user.getUsername());
        CheckUsernameAlreadyExists(user.getUsername());
        CheckEmailAlreadyExists(user.getEmail());
    }

    public void validateOnUpdate(final User user) {
        if (user.getUsername() != null) {
            CheckUsernameMinLength(user.getUsername());
            CheckUsernameAlreadyExists(user.getUsername());
        }
        if (user.getEmail() != null) {
            CheckEmailFormat(user.getEmail());
            CheckEmailAlreadyExists(user.getEmail());
        }
    }

    private void CheckEmailFormat(final String email) {
        String regex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);

        if(!m.matches()) {
            throw new IllegalStateException("Invalid email format.");
        }
    }

    private void CheckUsernameMinLength(final String username) {
        if (username.trim().length() < USERNAME_MIN_LENGTH) {
            throw new IllegalStateException("Username must be at least " + USERNAME_MIN_LENGTH + " characters long.");
        }
    }

    private void CheckEmailAlreadyExists(final String email) {
        if (userService.existsByEmail(email)) {
            throw new IllegalStateException("A user with this email already exists.");
        }
    }

    private void CheckUsernameAlreadyExists(final String username) {
        if (userService.existsByUsername(username)) {
            throw new IllegalStateException("A user with this username already exists.");
        }
    }
}
