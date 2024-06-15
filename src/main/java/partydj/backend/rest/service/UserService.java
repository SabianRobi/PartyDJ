package partydj.backend.rest.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import partydj.backend.rest.entity.User;
import partydj.backend.rest.entity.enums.PartyRole;
import partydj.backend.rest.entity.enums.UserType;
import partydj.backend.rest.entity.error.NotUniqueException;
import partydj.backend.rest.entity.error.RequiredFieldInvalidException;
import partydj.backend.rest.entity.request.DeleteUserRequest;
import partydj.backend.rest.entity.request.SaveUserRequest;
import partydj.backend.rest.entity.request.UpdateUserDetailsRequest;
import partydj.backend.rest.entity.request.UpdateUserPasswordRequest;
import partydj.backend.rest.entity.response.UserResponse;
import partydj.backend.rest.mapper.UserMapper;
import partydj.backend.rest.repository.UserRepository;
import partydj.backend.rest.validation.constraint.Exists;
import partydj.backend.rest.validation.constraint.NewPasswordsMatch;
import partydj.backend.rest.validation.constraint.SameUser;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Validated
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PartyService partyService;

    @Lazy
    @Autowired
    private SpotifyCredentialService spotifyCredentialService;

    @Autowired
    private UserMapper userMapper;

    // Repository handlers
    public User save(final User user) {
        return tryToSave(user, (Object) null);
    }

    @Exists(type = "User")
    public User findByUsername(final String username) {
        return userRepository.findByUsername(username);
    }

    public Set<User> saveAll(final Set<User> users) {
        return users.stream().map(user -> tryToSave(user, (Object) null)).collect(Collectors.toSet());
    }

    public User tryToSave(final User user, final SaveUserRequest userRequest) {
        return tryToSaveInner(user, userRequest.getUsername(), userRequest.getEmail());
    }

    public User tryToSave(final User user, final UpdateUserDetailsRequest userRequest) {
        return tryToSaveInner(user, userRequest.getUsername(), userRequest.getEmail());
    }

    public User tryToSave(final User user, final Object myNull) {
        return tryToSaveInner(user, null, null);
    }

    private User tryToSaveInner(final User user, final String username, final String email) {
        try {
            return userRepository.save(user);
        } catch (final DataIntegrityViolationException ex) {
            if (ex.getMessage().contains("Duplicate entry")) {
                String wrongValue = ex.getMessage().split("'")[1];

                if ((username != null && Objects.equals(wrongValue, username)) ||
                        (username == null && Objects.equals(wrongValue, user.getUsername()))) {
                    throw new NotUniqueException("username", "Already taken.");
                }
                if ((email != null && Objects.equals(wrongValue, email)) ||
                        (email == null && Objects.equals(wrongValue, user.getEmail()))) {
                    throw new NotUniqueException("email", "Already in use.");
                }

            }
            throw new IllegalStateException("Cannot save entity.");
        }
    }

    // Controller handlers

    public UserResponse register(final SaveUserRequest userRequest) {
        User user = User.builder()
                .email(userRequest.getEmail().trim())
                .username(userRequest.getUsername().trim())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .userType(UserType.NORMAL)
                .addedTracks(new HashSet<>())
                .build();

        return userMapper.mapUserToUserResponse(tryToSave(user, userRequest));
    }

    @Transactional
    @SameUser
    public UserResponse delete(final User loggedInUser, final String toBeDeletedUsername, final DeleteUserRequest deleteUserRequest) {
        if(!passwordEncoder.matches(deleteUserRequest.getPassword(), loggedInUser.getPassword())) {
            throw new RequiredFieldInvalidException("Incorrect password.");
        }

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

        if (loggedInUser.getSpotifyCredential() != null) {
            spotifyCredentialService.delete(loggedInUser.getSpotifyCredential());
        }

        userRepository.delete(loggedInUser);
        return userMapper.mapUserToUserResponse(loggedInUser);
    }

    @SameUser
    public UserResponse updateDetails(final User loggedInUser, final String givenUsername, final UpdateUserDetailsRequest newData) {
        loggedInUser.setEmail(newData.getEmail().trim());
        loggedInUser.setUsername(newData.getUsername().trim());

        return userMapper.mapUserToUserResponse(tryToSave(loggedInUser, newData));
    }

    @SameUser
    public UserResponse updatePassword(final User loggedInUser,
                                       final String givenUsername,
                                       @NewPasswordsMatch final UpdateUserPasswordRequest updateUserPasswordRequest) {

        if (!passwordEncoder.matches(updateUserPasswordRequest.getCurrentPassword(), loggedInUser.getPassword())) {
            throw new RequiredFieldInvalidException("Incorrect current password.");
        }

        loggedInUser.setPassword(passwordEncoder.encode(updateUserPasswordRequest.getPassword()));
        tryToSave(loggedInUser, (Object) null);

        return userMapper.mapUserToUserResponse(loggedInUser);
    }

    public UserResponse getUserInfo(final User toGetUser) {
        return userMapper.mapUserToUserResponse(toGetUser);
    }
}
