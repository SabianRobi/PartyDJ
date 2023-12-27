package partydj.backend.rest.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import partydj.backend.rest.entity.SpotifyCredential;
import partydj.backend.rest.entity.User;
import partydj.backend.rest.entity.response.SpotifyCredentialResponse;
import partydj.backend.rest.entity.response.SpotifyLoginUriResponse;
import partydj.backend.rest.helper.DataGenerator;
import partydj.backend.rest.mapper.SpotifyCredentialMapper;
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

    @Mock
    private SpotifyCredentialMapper spotifyCredentialMapper;

    @InjectMocks
    private SpotifyCredentialService spotifyCredentialService;

    final private User user;
    final private SpotifyCredential spotifyCredential;
    final private SpotifyCredentialResponse spotifyCredentialResponse;

    SpotifyCredentialServiceTest() {
        user = DataGenerator.generateUser();
        spotifyCredential = DataGenerator.generateSpotifyCredential(user);
        spotifyCredentialResponse = DataGenerator.generateSpotifyCredentialResponse(spotifyCredential);

        user.setSpotifyCredential(spotifyCredential);
    }

    @Test
    void shouldSaveSpotifyCredential() {
        when(spotifyCredentialRepository.save(any())).thenReturn(spotifyCredential);

        final SpotifyCredential savedCredential = spotifyCredentialService.save(spotifyCredential);

        assertThat(savedCredential).isSameAs(spotifyCredential);
    }

    @Test
    void givenSpotifyCredential_whenFindByOwner_thenSuccess() {
        when(spotifyCredentialRepository.findByOwner(any())).thenReturn(spotifyCredential);

        final SpotifyCredential foundCredential = spotifyCredentialService.findByOwner(user);

        assertThat(foundCredential).isSameAs(spotifyCredential);
    }

    @Test
    void givenSpotifyCredential_whenFindByState_thenSuccess() {
        final UUID state = UUID.randomUUID();
        when(spotifyCredentialRepository.findByState(any())).thenReturn(spotifyCredential);

        final SpotifyCredential foundCredential = spotifyCredentialService.findByState(state);

        assertThat(foundCredential).isSameAs(spotifyCredential);
    }

    @Test
    void givenSpotifyCredential_whenDelete_thenSuccess() {
        final SpotifyCredential deletedCredential = spotifyCredentialService.delete(spotifyCredential);

        assertThat(deletedCredential).isSameAs(spotifyCredential);
    }

    @Test
    void givenUserWithoutSpotify_whenRequestsLoginUri_thenSuccess() {
        final URI loginUri = URI.create("https://login.uri");
        when(spotifyService.generateLoginUri(any())).thenReturn(loginUri);

        final SpotifyLoginUriResponse response = spotifyCredentialService.getLoginUri(user);

        assertThat(response.getUri()).isSameAs(loginUri.toString());
    }

    @Test
    void givenUserWithoutSpotify_whenRequestsLoginUriMultipleTimes_thenSuccess() {
        final SpotifyCredential spotifyCredential = DataGenerator.generateSpotifyCredentialWithOnlyState(user);
        final URI loginUri = URI.create("https://login.uri");
        when(spotifyCredentialRepository.findByOwner(any())).thenReturn(spotifyCredential);
        when(spotifyService.generateLoginUri(any())).thenReturn(loginUri);

        final SpotifyLoginUriResponse response = spotifyCredentialService.getLoginUri(user);

        assertThat(response.getUri()).isSameAs(loginUri.toString());
    }

    @Test
    void givenUserLoggedInWithSpotify_whenLogout_thenSuccess() {
        when(spotifyCredentialRepository.findByOwner(any())).thenReturn(spotifyCredential);
        when(spotifyCredentialMapper.mapCredentialToCredentialResponse(any())).thenReturn(spotifyCredentialResponse);

        final SpotifyCredentialResponse response = spotifyCredentialService.logout(user);

        assertThat(response).isSameAs(spotifyCredentialResponse);
        assertThat(user.getSpotifyCredential()).isNull();
    }

    @Test
    void givenUserLoggedInWithSpotify_whenGetToken_thenSuccess() {
        when(spotifyCredentialRepository.findByOwner(any())).thenReturn(spotifyCredential);
        when(spotifyCredentialMapper.mapCredentialToCredentialResponse(any())).thenReturn(spotifyCredentialResponse);


        final SpotifyCredentialResponse response = spotifyCredentialService.getToken(user);

        assertThat(response.getToken()).isSameAs(spotifyCredential.getToken());
    }

    @Test
    void givenSpotifyResponse_whenProcessCallback_thenSuccess() {
        final String code = "code";
        final UUID state = UUID.randomUUID();
        when(spotifyCredentialRepository.findByState(any())).thenReturn(spotifyCredential);
        when(spotifyService.processCallback(any(), any())).thenReturn(spotifyCredential);
        when(spotifyCredentialMapper.mapCredentialToCredentialResponse(any())).thenReturn(spotifyCredentialResponse);

        final SpotifyCredentialResponse response = spotifyCredentialService.processCallback(code, state);

        assertThat(response.getToken()).isSameAs(spotifyCredential.getToken());
    }

    @Test
    void givenUserWithSpotify_whenRefreshToken_thenSuccess() {
        final SpotifyCredential refreshedCredential = DataGenerator.generateSpotifyCredential(user, "refreshed");
        spotifyCredentialResponse.setToken(refreshedCredential.getToken());
        when(spotifyCredentialRepository.findByOwner(any())).thenReturn(spotifyCredential);
        when(spotifyService.refreshToken(any())).thenReturn(refreshedCredential);
        when(spotifyCredentialMapper.mapCredentialToCredentialResponse(any())).thenReturn(spotifyCredentialResponse);

        final SpotifyCredentialResponse response = spotifyCredentialService.refreshToken(user);

        assertThat(response.getToken()).isSameAs(refreshedCredential.getToken());
    }
}
