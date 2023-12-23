package partydj.backend.rest.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import partydj.backend.rest.domain.Artist;
import partydj.backend.rest.domain.Party;
import partydj.backend.rest.domain.TrackInQueue;
import partydj.backend.rest.domain.User;
import partydj.backend.rest.domain.enums.PartyRole;
import partydj.backend.rest.domain.enums.UserType;
import partydj.backend.rest.domain.error.NotUniqueException;
import partydj.backend.rest.domain.request.UserRequest;
import partydj.backend.rest.helper.DataGenerator;
import partydj.backend.rest.repository.UserRepository;

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

    @InjectMocks
    private UserService userService;

    @Test
    void givenNewUser_whenRegister_thenSuccess() {
        final User user = DataGenerator.generateUserWithId();
        final UserRequest userRequest = DataGenerator.generateUserRequest();
        when(userRepository.save(any(User.class))).thenReturn(user);

        final User registeredUser = userService.register(userRequest);

        assertThat(registeredUser).isNotNull();
        assertThat(registeredUser.getUserType()).isEqualTo(UserType.NORMAL);
    }

    @Test
    void givenNewUser_whenRegisterWithUsedEmail_thenTrowsException() {
        final UserRequest userRequest = DataGenerator.generateUserRequest();
        final String message = "... Duplicate entry '" + userRequest.getEmail() + "' for key ...";
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException(message));

        assertThrows(NotUniqueException.class, () -> userService.register(userRequest));
    }

    @Test
    void givenNewUser_whenRegisterWithUsedUsername_thenTrowsException() {
        final UserRequest userRequest = DataGenerator.generateUserRequest();
        final String message = "... Duplicate entry '" + userRequest.getUsername() + "' for key ...";
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException(message));

        assertThrows(NotUniqueException.class, () -> userService.register(userRequest));
    }

    @Test
    void givenNewUser_whenRegister_thenTrowsException() {
        final UserRequest userRequest = DataGenerator.generateUserRequest();
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException("Unknown error."));

        assertThrows(IllegalStateException.class, () -> userService.register(userRequest));
    }

    @Test
    void givenUser_whenSave_thenSuccess() {
        final User user = DataGenerator.generateUserWithId();
        when(userRepository.save(any())).thenReturn(user);

        final User savedUser = userService.save(user);

        assertThat(savedUser).isSameAs(user);
    }

    @Test
    void givenUser_whenSave_thenThrowsException() {
        final User user = DataGenerator.generateUserWithId();
        when(userRepository.save(any())).thenThrow(new DataIntegrityViolationException("Unknown error."));

        assertThrows(IllegalStateException.class, () -> userService.save(user));
    }

    @Test
    void givenUser_whenFindByUsername_thenSuccess() {
        final User user = DataGenerator.generateUserWithId();
        when(userRepository.findByUsername(any())).thenReturn(user);

        final User foundUser = userService.findByUsername(user.getUsername());

        assertThat(foundUser).isSameAs(user);
    }

    @Test
    void givenUser_whenFindByUsername_thenThrowsException() {
        assertThrows(EntityNotFoundException.class, () -> userService.findByUsername("someone"));
    }

    @Test
    void givenUsers_whenSaveAll_thenSuccess() {
        final User user1 = DataGenerator.generateUserWithId("1");
        final User user2 = DataGenerator.generateUserWithId("2");
        final HashSet<User> users = new HashSet<>(Set.of(user1, user2));
        when(userRepository.saveAll(any())).thenReturn(users);

        final Set<User> savedUsers = userService.saveAll(users);

        assertThat(savedUsers).isSameAs(users);
    }

    @Test
    void givenUsers_whenSaveAll_thenThrowsException() {
        final User user1 = DataGenerator.generateUserWithId("1");
        final User user2 = DataGenerator.generateUserWithId("2");
        final HashSet<User> users = new HashSet<>(Set.of(user1, user2));
        when(userRepository.saveAll(any())).thenThrow(new DataIntegrityViolationException("Unknown error."));

        assertThrows(IllegalStateException.class, () -> userService.saveAll(users));
    }

    @Test
    void givenUser_whenDelete_thenSuccess() {
        final User user = DataGenerator.generateUserWithId();

        final User deletedUser = userService.delete(user, user.getUsername());

        assertThat(deletedUser).isSameAs(user);
    }

    @Test
    void givenUser_whenDeleteOtherUser_thenAccessDeniedException() {
        final User user1 = DataGenerator.generateUserWithId();
        final User user2 = DataGenerator.generateUserWithId("2");

        assertThrows(AccessDeniedException.class, () -> userService.delete(user1, user2.getUsername()));
    }

    @Test
    void givenUserInPartyAsParticipant_whenDelete_thenSuccess() {
        final User user = DataGenerator.generateUserWithId();
        final Party party = DataGenerator.generateParty("", Set.of(user));
        final Artist artist = DataGenerator.generateArtist();
        final TrackInQueue track = DataGenerator.generateTrackInQueue("", party, user, Set.of(artist));
        user.setParty(party);
        user.setPartyRole(PartyRole.PARTICIPANT);
        user.addAddedTrack(track);
        party.addTrackToQueue(track);

        final User deletedUser = userService.delete(user, user.getUsername());

        assertThat(deletedUser).isSameAs(user);
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
        final UserRequest userRequest = DataGenerator.generateUserRequest();
        userRequest.setUsername("otherUsername");
        when(userRepository.save(any())).thenReturn(user);

        final User updatedUser = userService.update(user, user.getUsername(), userRequest);

        assertThat(updatedUser.getUsername()).isSameAs(userRequest.getUsername());
    }

    @Test
    void givenUser_whenUpdateOtherUser_thenAccessDeniedException() {
        final User user1 = DataGenerator.generateUserWithId("1");
        final User user2 = DataGenerator.generateUserWithId("2");
        final UserRequest userRequest = DataGenerator.generateUserRequest();

        assertThrows(AccessDeniedException.class, () -> userService.update(user1, user2.getUsername(), userRequest));
    }
}
