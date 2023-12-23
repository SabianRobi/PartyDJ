package partydj.backend.rest.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import partydj.backend.rest.domain.*;
import partydj.backend.rest.domain.enums.PartyRole;
import partydj.backend.rest.domain.enums.UserType;
import partydj.backend.rest.domain.error.NotUniqueException;
import partydj.backend.rest.domain.request.UserRequest;
import partydj.backend.rest.helper.DataGenerator;
import partydj.backend.rest.repository.UserRepository;
import partydj.backend.rest.validation.UserValidator;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PartyService partyService;

    @Mock
    private UserValidator validator;

    @InjectMocks
    private UserService userService;

    @Test
    void givenNewUser_whenRegister_thenSuccess() {
        final UserRequest userRequest = DataGenerator.generateUserRequest();
        final User user = DataGenerator.generateUserWithId();
        when(userRepository.save(any(User.class))).thenReturn(user);

        final User registeredUser = userService.register(userRequest);

        assertThat(registeredUser).isNotNull();
        assertThat(registeredUser.getUserType()).isEqualTo(UserType.NORMAL);
    }

    @Test
    void givenUser_whenSave_thenSuccess() {
        final User user = DataGenerator.generateUserWithId();
        when(userRepository.save(any())).thenReturn(user);

        final User savedUser = userService.save(user);

        assertThat(savedUser).isSameAs(user);
    }

    @Test
    void givenUser_whenFindByUsername_thenSuccess() {
        final User user = DataGenerator.generateUserWithId();
        when(userRepository.findByUsername(any())).thenReturn(user);

        final User foundUser = userService.findByUsername(user.getUsername());

        assertThat(foundUser).isSameAs(user);
    }

    @Test
    void givenUsers_whenSaveAll_thenSuccess() {
        final User user1 = DataGenerator.generateUserWithId("1");
        final User user2 = DataGenerator.generateUserWithId("2");
        final HashSet<User> users = new HashSet<>(Set.of(user1, user2));
        when(userRepository.save(any())).thenReturn(user1).thenReturn(user2);

        final Set<User> savedUsers = userService.saveAll(users);

        assertThat(savedUsers).containsAll(users);
    }

    @Test
    void givenUserAndUserRequestButUsernameAlreadyUsed_whenTryToSave_thenTrowsNotUniqueException() {
        final User user = DataGenerator.generateUserWithId();
        final UserRequest userRequest = DataGenerator.generateUserRequest();
        final String message = "... Duplicate entry '" + userRequest.getUsername() + "' for key ...";
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException(message));

        assertThrows(NotUniqueException.class, () -> userService.tryToSave(user, userRequest));
    }

    @Test
    void givenUserButUsernameAlreadyUsed_whenTryToSave_thenTrowsNotUniqueException() {
        final User user = DataGenerator.generateUserWithId();
        final String message = "... Duplicate entry '" + user.getUsername() + "' for key ...";
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException(message));

        assertThrows(NotUniqueException.class, () -> userService.tryToSave(user, null));
    }

    @Test
    void givenUserAndUserRequestButEmailAlreadyUsed_whenTryToSave_thenTrowsNotUniqueException() {
        final User user = DataGenerator.generateUserWithId();
        final UserRequest userRequest = DataGenerator.generateUserRequest();
        final String message = "... Duplicate entry '" + userRequest.getEmail() + "' for key ...";
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException(message));

        assertThrows(NotUniqueException.class, () -> userService.tryToSave(user, userRequest));
    }

    @Test
    void givenUserButEmailAlreadyUsed_whenTryToSave_thenTrowsNotUniqueException() {
        final User user = DataGenerator.generateUserWithId();
        final String message = "... Duplicate entry '" + user.getEmail() + "' for key ...";
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException(message));

        assertThrows(NotUniqueException.class, () -> userService.tryToSave(user, null));
    }

    @Test
    void givenUser_whenTryToSave_thenTrowsIllegalStateException() {
        final User user = DataGenerator.generateUserWithId();
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException("Unknown error."));

        assertThrows(IllegalStateException.class, () -> userService.tryToSave(user, null));
    }

    @Test
    void givenUserNotInParty_whenDelete_thenSuccess() {
        final User user = DataGenerator.generateUserWithId();

        final User deletedUser = userService.delete(user, user.getUsername());

        assertThat(deletedUser).isSameAs(user);
    }

    @Test
    void givenUserInPartyAsParticipant_whenDelete_thenSuccess() {
        final User user = DataGenerator.generateUserWithId();
        final Party party = DataGenerator.generateParty("", Set.of(user));
        final Artist artist = DataGenerator.generateArtist();
        final TrackInQueue track = DataGenerator.generateTrackInQueue("", party, user, Set.of(artist));
        final HashSet<TrackInQueue> tracks = new HashSet<>(Set.of(track));
        final HashSet<Track> tracksForArtist = new HashSet<>(Set.of(track));
        user.setParty(party);
        user.setPartyRole(PartyRole.PARTICIPANT);
        user.setAddedTracks(tracks);
        party.setTracksInQueue(tracks);
        artist.setTracks(tracksForArtist);

        final User deletedUser = userService.delete(user, user.getUsername());

        assertThat(deletedUser).isSameAs(user);
        assertThat(deletedUser.getAddedTracks()).isEmpty();
        assertThat(party.getTracksInQueue()).isEmpty();
        assertThat(artist.getTracks()).isEmpty();
    }

    @Test
    void givenUserInPartyAsCreator_whenDelete_thenSuccess() {
        final User user = DataGenerator.generateUserWithId();
        final Party party = DataGenerator.generateParty("", Set.of(user));
        user.setParty(party);
        user.setPartyRole(PartyRole.CREATOR);
        when(partyService.deleteByName(any(), any())).thenReturn(party);

        final User deletedUser = userService.delete(user, user.getUsername());

        assertThat(deletedUser).isSameAs(user);
    }

    @Test
    void givenUser_whenUpdate_thenSuccess() {
        final User user = DataGenerator.generateUserWithId();
        final String toBeUpdatedUsername = user.getUsername();
        final UserRequest userRequest = DataGenerator.generateUserRequest();
        userRequest.setUsername("otherUsername");
        when(passwordEncoder.encode(any())).thenReturn(user.getPassword());
        when(userRepository.save(any())).thenReturn(user);

        final User updatedUser = userService.update(user, toBeUpdatedUsername, userRequest);

        assertThat(updatedUser.getUsername()).isSameAs(userRequest.getUsername());
    }
}
