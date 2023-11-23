package partydj.backend.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import partydj.backend.rest.domain.User;
import partydj.backend.rest.domain.request.UpdateUserRequest;
import partydj.backend.rest.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(final User user) {
        user.setUsername(user.getUsername().trim());
        user.setEmail(user.getEmail().trim());
        user.setPassword(passwordEncoder.encode(user.getPassword().trim()));
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException("Cannot save entity");
        }
    }

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
        return userRepository.existsByEmail(email);
    }

    public User update(final User user, final UpdateUserRequest updatedUserInfos) {
        Optional.ofNullable(updatedUserInfos.getUsername()).ifPresent(username ->
                user.setUsername(username.trim()));
        Optional.ofNullable(updatedUserInfos.getEmail()).ifPresent(email ->
                user.setEmail(email.trim()));
        Optional.ofNullable(updatedUserInfos.getPassword()).ifPresent(password ->
                user.setPassword(passwordEncoder.encode(password.trim())));
        return userRepository.save(user);
    }

    public User findByUsername(final String username) {
        return userRepository.findByUsername(username);
    }

    public User findByEmail(final String email) {
        return userRepository.findByEmail(email);
    }
}
