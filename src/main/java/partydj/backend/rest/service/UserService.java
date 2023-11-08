package partydj.backend.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import partydj.backend.rest.domain.User;
import partydj.backend.rest.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User save(final User user) {
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException("Cannot save entity");
        }
    }

    public void delete(final User user) {
        userRepository.delete(user);
    }

    public User findById(final int userId) {
        return userRepository.findById(userId);
    }

    public boolean existsByUsername(final String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(final String email) {
        return  userRepository.existsByEmail(email);
    }

    public User update(final User user, final User updatedUserInfos) {
        Optional.ofNullable(updatedUserInfos.getUsername()).ifPresent(user::setUsername);
        Optional.ofNullable(updatedUserInfos.getEmail()).ifPresent(user::setEmail);
        Optional.ofNullable(updatedUserInfos.getPassword()).ifPresent(user::setPassword);
        return userRepository.save(user);
    }
}
