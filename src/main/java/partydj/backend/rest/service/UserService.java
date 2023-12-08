package partydj.backend.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import partydj.backend.rest.domain.User;
import partydj.backend.rest.domain.enums.PartyRole;
import partydj.backend.rest.domain.enums.UserType;
import partydj.backend.rest.domain.error.NotUniqueException;
import partydj.backend.rest.domain.request.RegisterUserRequest;
import partydj.backend.rest.domain.request.UpdateUserRequest;
import partydj.backend.rest.repository.UserRepository;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PartyService partyService;

    public User register(final RegisterUserRequest userRequest) {
        User user = User.builder()
                .email(userRequest.getEmail().trim())
                .username(userRequest.getUsername().trim())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .userType(UserType.NORMAL)
                .addedTracks(new HashSet<>())
                .build();

        try {
            return userRepository.save(user);
        } catch (final DataIntegrityViolationException ex) {
            if (ex.getMessage().contains("Duplicate entry")) {
                String wrongValue = ex.getMessage().split("'")[1];

                if (Objects.equals(wrongValue, userRequest.getUsername())) {
                    throw new NotUniqueException("username", "Already taken.");
                }
                if (Objects.equals(wrongValue, userRequest.getEmail())) {
                    throw new NotUniqueException("email", "Already in use.");
                }
            }
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
        if (user.getPartyRole() != null) {
            if (user.getPartyRole() == PartyRole.CREATOR) {
                partyService.delete(user.getParty());
            } else {
                user.getAddedTracks().forEach(track ->
                        user.getParty().removeTrackFromQueue(track));
                user.getParty().removeUser(user);
                partyService.save(user.getParty());
            }
        }

        userRepository.delete(user);
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

    public void saveAll(final Set<User> users) {
        userRepository.saveAll(users);
    }
}
