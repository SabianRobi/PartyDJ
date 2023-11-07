package partydj.backend.rest.validation;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import partydj.backend.rest.domain.User;
import partydj.backend.rest.service.UserService;

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
        if (user.getEmail().isEmpty()) {
            throw new IllegalStateException("Email cannot be empty.");
        }
        if (user.getPassword().isEmpty()) {
            throw new IllegalStateException("Password cannot be empty.");
        }
        if (user.getUsername().isEmpty()) {
            throw new IllegalStateException("Username cannot be empty.");
        }

        if (userService.existsByUsername(user.getUsername())) {
            throw new IllegalStateException("A user with this username already exists.");
        }
        if (userService.existsByEmail(user.getEmail())) {
            throw new IllegalStateException("A user with this email already exists.");
        }
    }
}
