package partydj.backend.rest.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import partydj.backend.rest.domain.User;
import partydj.backend.rest.domain.enums.PartyRole;
import partydj.backend.rest.domain.enums.UserType;
import partydj.backend.rest.domain.error.NotUniqueException;
import partydj.backend.rest.domain.request.UserRequest;
import partydj.backend.rest.repository.UserRepository;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PartyService partyService;

    public User register(final UserRequest userRequest) {
        User user = User.builder()
                .email(userRequest.getEmail().trim())
                .username(userRequest.getUsername().trim())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .userType(UserType.NORMAL)
                .addedTracks(new HashSet<>())
                .build();

        return tryToSave(user, userRequest);
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

    public User update(final String username, final User user, final UserRequest newData) {
        if (!Objects.equals(username, user.getUsername())) {
            throw new AccessDeniedException("You can not make changes to other user profiles.");
        }

        user.setEmail(newData.getEmail().trim());
        user.setUsername(newData.getUsername().trim());
        user.setPassword(passwordEncoder.encode(newData.getPassword()));

        return tryToSave(user, newData);
    }

    public User findByUsername(final String username) {
        final User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new EntityNotFoundException("User does not exists.");
        }

        return user;
    }

    public void saveAll(final Set<User> users) {
        userRepository.saveAll(users);
    }

    private User tryToSave(final User user, final UserRequest userRequest) {
        try {
            return userRepository.save(user);
        } catch (final DataIntegrityViolationException ex) {
            System.err.println(ex.getMessage());

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
}
