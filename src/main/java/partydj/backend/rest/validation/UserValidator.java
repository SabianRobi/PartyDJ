package partydj.backend.rest.validation;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import partydj.backend.rest.domain.User;

@Service
public class UserValidator {
    public void validateOnGet(User user) {
        if(user == null) {
            throw new EntityNotFoundException("User does not exists.");
        }
    }
}
