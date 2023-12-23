package partydj.backend.rest.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import partydj.backend.rest.domain.User;
import partydj.backend.rest.domain.enums.PartyRole;
import partydj.backend.rest.domain.enums.UserType;
import partydj.backend.rest.domain.error.NotUniqueException;
import partydj.backend.rest.domain.request.UserRequest;
import partydj.backend.rest.repository.UserRepository;
import partydj.backend.rest.validation.UserValidator;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PartyService partyService;

    @Autowired
    private UserValidator validator;

    // Repository handlers

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
        return tryToSave(user, null);
    }

    public User findByUsername(final String username) {
        final User user = userRepository.findByUsername(username);

        validator.verifyNotNull(user);

        return user;
    }

    public Set<User> saveAll(final Set<User> users) {
        return users.stream().map(user -> tryToSave(user, null)).collect(Collectors.toSet());
    }

    public User tryToSave(final User user, final UserRequest userRequest) {
        try {
            return userRepository.save(user);
        } catch (final DataIntegrityViolationException ex) {
            if (ex.getMessage().contains("Duplicate entry")) {
                String wrongValue = ex.getMessage().split("'")[1];

                if ((userRequest != null && Objects.equals(wrongValue, userRequest.getUsername())) ||
                        (userRequest == null && Objects.equals(wrongValue, user.getUsername()))) {
                    throw new NotUniqueException("username", "Already taken.");
                }
                if ((userRequest != null && Objects.equals(wrongValue, userRequest.getEmail())) ||
                        (userRequest == null && Objects.equals(wrongValue, user.getEmail()))) {
                    throw new NotUniqueException("email", "Already in use.");
                }

            }
            throw new IllegalStateException("Cannot save entity.");
        }
    }

    // Controller handlers

    @Transactional
    public User delete(final User loggedInUser, final String toBeDeletedUsername) {
        validator.verifySameUser(loggedInUser, toBeDeletedUsername);

        if (loggedInUser.getPartyRole() != null) {
            if (loggedInUser.getPartyRole() == PartyRole.CREATOR) {
                partyService.deleteByName(loggedInUser, loggedInUser.getParty().getName());
            } else {
                loggedInUser.getAddedTracks().forEach(track -> {
                    loggedInUser.getParty().removeTrackFromQueue(track);
                    track.getArtists().forEach(artist -> artist.removeTrack(track));
                });
                loggedInUser.getParty().removeUser(loggedInUser);
                partyService.save(loggedInUser.getParty());
            }
        }

        userRepository.delete(loggedInUser);
        return loggedInUser;
    }

    public User update(final User loggedInUser, final String givenUsername, final UserRequest newData) {
        validator.verifySameUser(loggedInUser, givenUsername);

        loggedInUser.setEmail(newData.getEmail().trim());
        loggedInUser.setUsername(newData.getUsername().trim());
        loggedInUser.setPassword(passwordEncoder.encode(newData.getPassword()));

        return tryToSave(loggedInUser, newData);
    }
}
