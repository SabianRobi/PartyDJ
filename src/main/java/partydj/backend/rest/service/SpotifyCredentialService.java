package partydj.backend.rest.service;

import jakarta.persistence.EntityNotFoundException;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import partydj.backend.rest.domain.SpotifyCredential;
import partydj.backend.rest.domain.User;
import partydj.backend.rest.domain.error.ThirdPartyApiException;
import partydj.backend.rest.repository.SpotifyCredentialRepository;
import partydj.backend.rest.validation.SpotifyCredentialValidator;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.UUID;

@Service
public class SpotifyCredentialService {
    @Autowired
    private SpotifyCredentialRepository repository;

    @Autowired
    private UserService userService;

    @Autowired
    private Map<String, String> spotifyConfigs;

    @Autowired
    private SpotifyCredentialValidator validator;

    private final SpotifyApi spotifyApi;

    @Autowired
    public SpotifyCredentialService(final Map<String, String> spotifyConfigs) {
        spotifyApi = new SpotifyApi.Builder()
                .setClientId(spotifyConfigs.get("client-id"))
                .setClientSecret(spotifyConfigs.get("client-secret"))
                .setRedirectUri(URI.create(spotifyConfigs.get("redirect-uri")))
                .build();
    }

    // Repository handlers

    public SpotifyCredential findByOwner(final User owner) {
        final SpotifyCredential spotifyCredential = repository.findByOwner(owner);

        if (spotifyCredential == null) {
            throw new IllegalStateException("You have not connected your Spotify account.");
        }

        return spotifyCredential;
    }

    public SpotifyCredential findByState(final UUID state) {
        final SpotifyCredential spotifyCredential = repository.findByState(state.toString());

        if (spotifyCredential == null) {
            throw new EntityNotFoundException("Failed to log in with Spotify. Please try again.");
        }

        return spotifyCredential;
    }

    public SpotifyCredential save(final SpotifyCredential spotifyCredential) {
        return repository.save(spotifyCredential);
    }

    public void delete(final SpotifyCredential spotifyCredential) {
        repository.delete(spotifyCredential);
    }


    // Controller handlers

    public String getLoginUri(final User loggedInUser) {
        SpotifyCredential spotifyCredential = repository.findByOwner(loggedInUser);

        if (spotifyCredential != null && spotifyCredential.getToken() != null) {
            throw new IllegalStateException("You have already connected your Spotify account.");
        }

        final String state = UUID.randomUUID().toString();

        if (spotifyCredential != null) {
            spotifyCredential.setState(state);
        } else {
            spotifyCredential = SpotifyCredential.builder()
                    .state(state)
                    .owner(loggedInUser)
                    .build();
        }

        save(spotifyCredential);
        loggedInUser.setSpotifyCredential(spotifyCredential);
        userService.save(loggedInUser);

        final AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                .scope(spotifyConfigs.get("scopes"))
                .show_dialog(true)
                .state(state)
                .build();
        final URI uri = authorizationCodeUriRequest.execute();
        return uri.toString();
    }

    public SpotifyCredential processCallback(final String code, final UUID state) {
        final SpotifyCredential spotifyCredential = findByState(state);
        final AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code).build();

        try {
            // Get user token
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

            // Save tokens
            spotifyCredential.setToken(authorizationCodeCredentials.getAccessToken());
            spotifyCredential.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
            spotifyCredential.setState(null);

            save(spotifyCredential);
        } catch (IOException | SpotifyWebApiException | ParseException ex) {
            throw new ThirdPartyApiException("Failed to log in with Spotify: " + ex.getMessage());
        }

        return spotifyCredential;
    }

    public SpotifyCredential logout(final User loggedInUser) {
        final SpotifyCredential spotifyCredential = findByOwner(loggedInUser);

        loggedInUser.setSpotifyCredential(null);
        userService.save(loggedInUser);
        delete(spotifyCredential);

        return spotifyCredential;
    }

    public SpotifyCredential getToken(final User loggedInUser) {
        final SpotifyCredential spotifyCredential = findByOwner(loggedInUser);

        validator.verifyLoggedIn(spotifyCredential);

        return spotifyCredential;
    }

    public SpotifyCredential refreshToken(final User loggedInUser) {
        final SpotifyCredential spotifyCredential = findByOwner(loggedInUser);

        validator.verifyLoggedIn(spotifyCredential);

        spotifyApi.setRefreshToken(spotifyCredential.getRefreshToken());
        final AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest =
                spotifyApi.authorizationCodeRefresh().build();
        try {
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();

            spotifyCredential.setToken(authorizationCodeCredentials.getAccessToken());
            save(spotifyCredential);
        } catch (IOException | SpotifyWebApiException | ParseException ex) {
            throw new ThirdPartyApiException("Failed to refresh Spotify token: " + ex.getMessage());
        }

        return spotifyCredential;
    }
}
