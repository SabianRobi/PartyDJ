package partydj.backend.rest.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import partydj.backend.rest.entity.SpotifyCredential;
import partydj.backend.rest.entity.User;
import partydj.backend.rest.helper.DataGenerator;
import partydj.backend.rest.repository.SpotifyCredentialRepository;
import partydj.backend.rest.validation.SpotifyCredentialValidator;

import java.net.URI;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SpotifyCredentialServiceTest {
    @Mock
    private SpotifyCredentialRepository spotifyCredentialRepository;

    @Mock
    private UserService userService;

    @Mock
    private SpotifyCredentialValidator validator;

    @Mock
    private SpotifyService spotifyService;

    @InjectMocks
    private SpotifyCredentialService spotifyCredentialService;

    @Test
    void shouldSaveSpotifyCredential() {
        final User user = DataGenerator.generateUser();
        final SpotifyCredential spotifyCredential = DataGenerator.generateSpotifyCredential(user);
        when(spotifyCredentialRepository.save(any())).thenReturn(spotifyCredential);

        final SpotifyCredential savedCredential = spotifyCredentialService.save(spotifyCredential);

        assertThat(savedCredential).isSameAs(spotifyCredential);
    }

    @Test
    void givenSpotifyCredential_whenFindByOwner_thenSuccess() {
        final User user = DataGenerator.generateUser();
        final SpotifyCredential spotifyCredential = DataGenerator.generateSpotifyCredential(user);
        when(spotifyCredentialRepository.findByOwner(any())).thenReturn(spotifyCredential);

        final SpotifyCredential foundCredential = spotifyCredentialService.findByOwner(user);

        assertThat(foundCredential).isSameAs(spotifyCredential);
    }

    @Test
    void givenSpotifyCredential_whenFindByState_thenSuccess() {
        final UUID state = UUID.randomUUID();
        final User user = DataGenerator.generateUser();
        final SpotifyCredential spotifyCredential = DataGenerator.generateSpotifyCredential(user);
        when(spotifyCredentialRepository.findByState(any())).thenReturn(spotifyCredential);

        final SpotifyCredential foundCredential = spotifyCredentialService.findByState(state);

        assertThat(foundCredential).isSameAs(spotifyCredential);
    }

    @Test
    void givenSpotifyCredential_whenDelete_thenSuccess() {
        final User user = DataGenerator.generateUser();
        final SpotifyCredential spotifyCredential = DataGenerator.generateSpotifyCredential(user);

        final SpotifyCredential deletedCredential = spotifyCredentialService.delete(spotifyCredential);

        assertThat(deletedCredential).isSameAs(spotifyCredential);
    }

    @Test
    void givenUserWithoutSpotify_whenRequestsLoginUri_thenSuccess() {
        final User user = DataGenerator.generateUser();
        final URI loginUri = URI.create("https://login.uri");
        when(spotifyService.generateLoginUri(any())).thenReturn(loginUri);

        final String receivedLoginUri = spotifyCredentialService.getLoginUri(user);

        assertThat(receivedLoginUri).isSameAs(loginUri.toString());
    }

    @Test
    void givenUserWithoutSpotify_whenRequestsLoginUriMultipleTimes_thenSuccess() {
        final User user = DataGenerator.generateUser();
        final SpotifyCredential spotifyCredential = DataGenerator.generateSpotifyCredentialWithOnlyState(user);
        final URI loginUri = URI.create("https://login.uri");
        when(spotifyCredentialRepository.findByOwner(any())).thenReturn(spotifyCredential);
        when(spotifyService.generateLoginUri(any())).thenReturn(loginUri);

        final String receivedLoginUri = spotifyCredentialService.getLoginUri(user);

        assertThat(receivedLoginUri).isSameAs(loginUri.toString());
    }

    @Test
    void givenUserLoggedInWithSpotify_whenLogout_thenSuccess() {
        final User user = DataGenerator.generateUser();
        final SpotifyCredential spotifyCredential = DataGenerator.generateSpotifyCredential(user);
        user.setSpotifyCredential(spotifyCredential);
        when(spotifyCredentialRepository.findByOwner(any())).thenReturn(spotifyCredential);

        spotifyCredentialService.logout(user);

        assertThat(user.getSpotifyCredential()).isNull();
    }

    @Test
    void givenUserLoggedInWithSpotify_whenGetToken_thenSuccess() {
        final User user = DataGenerator.generateUser();
        final SpotifyCredential spotifyCredential = DataGenerator.generateSpotifyCredential(user);
        when(spotifyCredentialRepository.findByOwner(any())).thenReturn(spotifyCredential);

        final SpotifyCredential foundSpotifyCredential = spotifyCredentialService.getToken(user);

        assertThat(foundSpotifyCredential).isSameAs(spotifyCredential);
    }

    @Test
    void givenSpotifyResponse_whenProcessCallback_thenSuccess() {
        final String code = "code";
        final UUID state = UUID.randomUUID();
        final User user = DataGenerator.generateUser();
        final SpotifyCredential spotifyCredential = DataGenerator.generateSpotifyCredential(user);
        when(spotifyCredentialRepository.findByState(any())).thenReturn(spotifyCredential);
        when(spotifyService.processCallback(any(), any())).thenReturn(spotifyCredential);

        final SpotifyCredential processedCredential = spotifyCredentialService.processCallback(code, state);

        assertThat(processedCredential).isSameAs(spotifyCredential);
    }

    @Test
    void givenUserWithSpotify_whenRefreshToken_thenSuccess() {
        final User user = DataGenerator.generateUser();
        final SpotifyCredential spotifyCredential = DataGenerator.generateSpotifyCredential(user);
        final SpotifyCredential refreshedCredential = DataGenerator.generateSpotifyCredential(user, "refreshed");
        when(spotifyCredentialRepository.findByOwner(any())).thenReturn(spotifyCredential);
        when(spotifyService.refreshToken(any())).thenReturn(refreshedCredential);

        final SpotifyCredential refreshedToken = spotifyCredentialService.refreshToken(user);

        assertThat(refreshedToken).isSameAs(refreshedCredential);
    }
}
