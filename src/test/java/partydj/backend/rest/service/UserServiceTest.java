package partydj.backend.rest.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import partydj.backend.rest.entity.*;
import partydj.backend.rest.entity.enums.PartyRole;
import partydj.backend.rest.entity.error.NotUniqueException;
import partydj.backend.rest.entity.error.RequiredFieldInvalidException;
import partydj.backend.rest.entity.request.SaveUserRequest;
import partydj.backend.rest.entity.request.UpdateUserDetailsRequest;
import partydj.backend.rest.entity.request.UpdateUserPasswordRequest;
import partydj.backend.rest.entity.response.UserResponse;
import partydj.backend.rest.mapper.UserMapper;
import partydj.backend.rest.repository.UserRepository;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static partydj.backend.rest.helper.DataGenerator.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PartyService partyService;

    @Mock
    private SpotifyCredentialService spotifyCredentialService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    final private User user;

    UserServiceTest() {
        user = generateUser();
    }


    @Test
    void givenNewUser_whenRegister_thenSuccess() {
        final SaveUserRequest userRequest = generateSaveUserRequest(user);
        final UserResponse userResponse = generateUserResponse(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.mapUserToUserResponse(any())).thenReturn(userResponse);

        final UserResponse response = userService.register(userRequest);

        verifyUserAndResponse(response, user);
    }

    @Test
    void givenUser_whenSave_thenSuccess() {
        when(userRepository.save(any())).thenReturn(user);

        final User savedUser = userService.save(user);

        assertThat(savedUser).isSameAs(user);
    }

    @Test
    void givenUser_whenFindByUsername_thenSuccess() {
        when(userRepository.findByUsername(any())).thenReturn(user);

        final User foundUser = userService.findByUsername(user.getUsername());

        assertThat(foundUser).isSameAs(user);
    }

    @Test
    void givenUsers_whenSaveAll_thenSuccess() {
        final User user2 = generateUser("2");
        final HashSet<User> users = new HashSet<>(Set.of(user, user2));
        when(userRepository.save(any())).thenReturn(user).thenReturn(user2);

        final Set<User> savedUsers = userService.saveAll(users);

        assertThat(savedUsers).containsAll(users);
    }

    @Test
    void givenUserAndUserRequestButUsernameAlreadyUsed_whenTryToSave_thenTrowsNotUniqueException() {
        final SaveUserRequest userRequest = generateSaveUserRequest(user);
        final String message = "... Duplicate entry '" + userRequest.getUsername() + "' for key ...";
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException(message));

        assertThrows(NotUniqueException.class, () -> userService.tryToSave(user, userRequest));
    }

    @Test
    void givenUserButUsernameAlreadyUsed_whenTryToSave_thenTrowsNotUniqueException() {
        final String message = "... Duplicate entry '" + user.getUsername() + "' for key ...";
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException(message));

        assertThrows(NotUniqueException.class, () -> userService.tryToSave(user, (Object) null));
    }

    @Test
    void givenUserAndUserRequestButEmailAlreadyUsed_whenTryToSave_thenTrowsNotUniqueException() {
        final SaveUserRequest userRequest = generateSaveUserRequest(user);
        final String message = "... Duplicate entry '" + userRequest.getEmail() + "' for key ...";
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException(message));

        assertThrows(NotUniqueException.class, () -> userService.tryToSave(user, userRequest));
    }

    @Test
    void givenUserButEmailAlreadyUsed_whenTryToSave_thenTrowsNotUniqueException() {
        final String message = "... Duplicate entry '" + user.getEmail() + "' for key ...";
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException(message));

        assertThrows(NotUniqueException.class, () -> userService.tryToSave(user, (Object) null));
    }

    @Test
    void givenUser_whenTryToSave_thenTrowsIllegalStateException() {
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException("Unknown error."));

        assertThrows(IllegalStateException.class, () -> userService.tryToSave(user, (Object) null));
    }

    @Test
    void givenUserNotInParty_whenDelete_thenSuccess() {
        final UserResponse userResponse = generateUserResponse(user);
        when(userMapper.mapUserToUserResponse(any())).thenReturn(userResponse);

        final UserResponse response = userService.delete(user, user.getUsername());

        verifyUserAndResponse(response, user);
    }

    @Test
    void givenUserInPartyAsParticipant_whenDelete_thenSuccess() {
        final Party party = generateParty("", Set.of(user));
        final Artist artist = generateArtist();
        final TrackInQueue track = generateTrackInQueue("", party, user, Set.of(artist));
        final HashSet<TrackInQueue> tracks = new HashSet<>(Set.of(track));
        final HashSet<Track> tracksForArtist = new HashSet<>(Set.of(track));
        final UserResponse userResponse = generateUserResponse(user);
        user.setParty(party);
        user.setPartyRole(PartyRole.PARTICIPANT);
        user.setAddedTracks(tracks);
        party.setTracksInQueue(tracks);
        artist.setTracks(tracksForArtist);

        when(userMapper.mapUserToUserResponse(any())).thenReturn(userResponse);

        final UserResponse response = userService.delete(user, user.getUsername());

        verifyUserAndResponse(response, user);
        assertThat(user.getAddedTracks()).isEmpty();
        assertThat(party.getTracksInQueue()).isEmpty();
        assertThat(artist.getTracks()).isEmpty();
    }

    @Test
    void givenUserInPartyAsCreator_whenDelete_thenSuccess() {
        final Party party = generateParty("", Set.of(user));
        final UserResponse userResponse = generateUserResponse(user);
        user.setParty(party);
        user.setPartyRole(PartyRole.CREATOR);
        when(userMapper.mapUserToUserResponse(any())).thenReturn(userResponse);
        when(partyService.deleteByName(any(), any())).thenAnswer(invocation -> {
            user.setParty(null);
            user.setPartyRole(null);
            return null;
        });

        final UserResponse response = userService.delete(user, user.getUsername());

        verifyUserAndResponse(response, user);
        assertThat(user.getParty()).isNull();
        assertThat(user.getPartyRole()).isNull();
    }

    @Test
    void givenUserWithSpotify_whenDelete_thenDeletesSpotifyAlso() {
        final Party party = generateParty("", Set.of(user));
        final SpotifyCredential spotifyCredential = generateSpotifyCredential(user);
        user.setParty(party);
        user.setPartyRole(PartyRole.CREATOR);
        user.setSpotifyCredential(spotifyCredential);
        when(spotifyCredentialService.delete(any())).then(invocation -> {
            user.setSpotifyCredential(null);
            return null;
        });

        userService.delete(user, user.getUsername());

        assertThat(user.getSpotifyCredential()).isNull();
    }

    @Test
    void givenUser_whenUpdateUsername_thenSuccess() {
        final UserResponse userResponse = generateUserResponse(user);
        final UpdateUserDetailsRequest userRequest = generateUpdateUserDetailsRequest(user);
        userRequest.setUsername("otherUsername");
        userResponse.setUsername("otherUsername");
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.mapUserToUserResponse(any())).thenReturn(userResponse);

        final UserResponse response = userService.updateDetails(user, user.getUsername(), userRequest);

        assertThat(response.getUsername()).isSameAs(userRequest.getUsername());
    }

    @Test
    void givenUser_whenUpdateEmail_thenSuccess() {
        final UserResponse userResponse = generateUserResponse(user);
        final UpdateUserDetailsRequest userRequest = generateUpdateUserDetailsRequest(user);
        userRequest.setEmail("other@email.test");
        userResponse.setEmail("other@email.test");
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.mapUserToUserResponse(any())).thenReturn(userResponse);

        final UserResponse response = userService.updateDetails(user, user.getUsername(), userRequest);

        assertThat(response.getEmail()).isSameAs(userRequest.getEmail());
    }

    @Test
    void givenUser_whenUpdatePassword_thenSuccess() {
        final UserResponse userResponse = generateUserResponse(user);
        final UpdateUserPasswordRequest userRequest = generateUpdateUserPasswordRequest(user);
        when(passwordEncoder.encode(any())).thenReturn(user.getPassword());
        when(passwordEncoder.matches("password", "password")).thenReturn(true);
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.mapUserToUserResponse(any())).thenReturn(userResponse);

        final UserResponse response = userService.updatePassword(user, user.getUsername(), userRequest);

        verifyUserAndResponse(response, user);
    }

    @Test
    void givenUser_whenUpdatePassword_thenIncorrectCurrentPassword() {
        final UpdateUserPasswordRequest userRequest = generateUpdateUserPasswordRequest(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(RequiredFieldInvalidException.class, () ->
                userService.updatePassword(user, user.getUsername(), userRequest));
    }

    @Test
    void givenUser_whenGetUserInfo_thenSuccess() {
        final UserResponse userResponse = generateUserResponse(user);
        when(userMapper.mapUserToUserResponse(any())).thenReturn(userResponse);

        final UserResponse response = userService.getUserInfo(user);

        assertThat(response).isSameAs(userResponse);
    }


    private void verifyUserAndResponse(final UserResponse response, final User user) {
        assertThat(response.getId()).isEqualTo(user.getId());
        assertThat(response.getUsername()).isEqualTo(user.getUsername());
        assertThat(response.getEmail()).isEqualTo(user.getEmail());
    }
}
