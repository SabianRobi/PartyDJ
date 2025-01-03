package partydj.backend.rest.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import partydj.backend.rest.entity.SpotifyCredential;
import partydj.backend.rest.entity.User;
import partydj.backend.rest.entity.request.SetPlatformTokensRequest;
import partydj.backend.rest.entity.response.PlatformCredentialResponse;
import partydj.backend.rest.entity.response.PlatformLoginUriResponse;
import partydj.backend.rest.helper.DataGenerator;
import partydj.backend.rest.mapper.PlatformCredentialMapper;
import partydj.backend.rest.repository.SpotifyCredentialRepository;
import partydj.backend.rest.validation.SpotifyCredentialValidator;

import java.net.URI;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static partydj.backend.rest.helper.DataGenerator.generateSetSpotifyTokensRequest;

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
    private PlatformCredentialMapper platformCredentialMapper;

    @InjectMocks
    private SpotifyCredentialService spotifyCredentialService;

    final private User user;
    final private SpotifyCredential spotifyCredential;
    final private PlatformCredentialResponse platformCredentialResponse;

    SpotifyCredentialServiceTest() {
        user = DataGenerator.generateUser();
        spotifyCredential = DataGenerator.generateSpotifyCredential(user);
        platformCredentialResponse = DataGenerator.generateSpotifyCredentialResponse(spotifyCredential);

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
        final PlatformLoginUriResponse uriResponse = DataGenerator.generateSpotifyLoginUriResponse(loginUri);
        when(spotifyService.generateLoginUri(any())).thenReturn(uriResponse);

        final PlatformLoginUriResponse response = spotifyCredentialService.getLoginUri(user);

        assertThat(response).isSameAs(uriResponse);
        assertThat(response.getUri()).isSameAs(loginUri.toString());
    }

    @Test
    void givenUserWithoutSpotify_whenRequestsLoginUriMultipleTimes_thenSuccess() {
        final SpotifyCredential spotifyCredential = DataGenerator.generateSpotifyCredentialWithOnlyState(user);
        final URI loginUri = URI.create("https://login.uri");
        final PlatformLoginUriResponse uriResponse = DataGenerator.generateSpotifyLoginUriResponse(loginUri);
        when(spotifyCredentialRepository.findByOwner(any())).thenReturn(spotifyCredential);
        when(spotifyService.generateLoginUri(any())).thenReturn(uriResponse);

        final PlatformLoginUriResponse response = spotifyCredentialService.getLoginUri(user);

        assertThat(response.getUri()).isSameAs(loginUri.toString());
    }

    @Test
    void givenUserLoggedInWithSpotify_whenLogout_thenSuccess() {
        when(spotifyCredentialRepository.findByOwner(any())).thenReturn(spotifyCredential);
        when(platformCredentialMapper.mapCredentialToCredentialResponse(any())).thenReturn(platformCredentialResponse);

        final PlatformCredentialResponse response = spotifyCredentialService.logout(user);

        assertThat(response).isSameAs(platformCredentialResponse);
        assertThat(user.getSpotifyCredential()).isNull();
    }

    @Test
    void givenUserLoggedInWithSpotify_whenGetToken_thenSuccess() {
        when(spotifyCredentialRepository.findByOwner(any())).thenReturn(spotifyCredential);
        when(platformCredentialMapper.mapCredentialToCredentialResponse(any())).thenReturn(platformCredentialResponse);


        final PlatformCredentialResponse response = spotifyCredentialService.getToken(user);

        assertThat(response.getToken()).isSameAs(spotifyCredential.getToken());
    }

    @Test
    void givenSpotifyResponse_whenProcessCallback_thenSuccess() {
        final SetPlatformTokensRequest request = generateSetSpotifyTokensRequest();
        final PlatformCredentialResponse platformCredentialResponse =
                DataGenerator.generateSpotifyCredentialResponse(spotifyCredential);
        when(spotifyCredentialRepository.findByState(any())).thenReturn(spotifyCredential);
        when(spotifyService.processCallback(any(), any())).thenReturn(platformCredentialResponse);

        final PlatformCredentialResponse response = spotifyCredentialService.processCallback(request);

        assertThat(response.getToken()).isSameAs(spotifyCredential.getToken());
    }

    @Test
    void givenUserWithSpotify_whenRefreshToken_thenSuccess() {
        final SpotifyCredential refreshedCredential = DataGenerator.generateSpotifyCredential(user, "refreshed");
        platformCredentialResponse.setToken(refreshedCredential.getToken());
        final PlatformCredentialResponse refreshedPlatformCredentialResponse =
                DataGenerator.generateSpotifyCredentialResponse(refreshedCredential);
        when(spotifyCredentialRepository.findByOwner(any())).thenReturn(spotifyCredential);
        when(spotifyService.refreshToken(any())).thenReturn(refreshedPlatformCredentialResponse);

        final PlatformCredentialResponse response = spotifyCredentialService.refreshToken(user);

        assertThat(response.getToken()).isSameAs(refreshedCredential.getToken());
    }
}
