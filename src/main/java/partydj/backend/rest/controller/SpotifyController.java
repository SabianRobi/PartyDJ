package partydj.backend.rest.controller;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import partydj.backend.rest.domain.SpotifyCredential;
import partydj.backend.rest.domain.User;
import partydj.backend.rest.domain.error.ThirdPartyAPIError;
import partydj.backend.rest.domain.response.SpotifyCredentialResponse;
import partydj.backend.rest.mapper.SpotifyCredentialMapper;
import partydj.backend.rest.service.SpotifyCredentialService;
import partydj.backend.rest.service.UserService;
import partydj.backend.rest.validation.SpotifyCredentialValidator;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import static partydj.backend.rest.config.SpotifyConfig.*;

@RestController
@RequestMapping("/api/v1/platforms/spotify")
public class SpotifyController {

    @Autowired
    private UserService userService;

    @Autowired
    private SpotifyCredentialService spotifyCredentialService;

    @Autowired
    private SpotifyCredentialMapper spotifyCredentialMapper;

    @Autowired
    private SpotifyCredentialValidator spotifyCredentialValidator;

    private final SpotifyApi spotifyApi;

    public SpotifyController() {
        spotifyApi = new SpotifyApi.Builder()
                .setClientId(CLIENT_ID)
                .setClientSecret(CLIENT_SECRET)
                .setRedirectUri(URI.create(REDIRECT_URI))
                .build();
    }

    @GetMapping("/login")
    public String getLoginURI(final Authentication auth) {
        User loggedInUser = userService.findByUsername(auth.getName());
        SpotifyCredential spotifyCredential = spotifyCredentialService.findByOwner(loggedInUser);

        spotifyCredentialValidator.validateOnGetLoginURI(spotifyCredential);

        final String state = UUID.randomUUID().toString();

        if (spotifyCredential != null) {
            spotifyCredential.setState(state);
        } else {
            spotifyCredential = SpotifyCredential.builder()
                    .state(state)
                    .owner(loggedInUser)
                    .build();
        }

        spotifyCredentialService.save(spotifyCredential);
        loggedInUser.setSpotifyCredential(spotifyCredential);
        userService.save(loggedInUser);

        final AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                .scope(SCOPES)
                .show_dialog(true)
                .state(state)
                .build();
        URI uri = authorizationCodeUriRequest.execute();
        return uri.toString();
    }

    @GetMapping("/callback")
    public SpotifyCredentialResponse processCallback(@RequestParam("code") final String code,
                                                     @RequestParam("state") final String state) {
        SpotifyCredential spotifyCredential = spotifyCredentialService.findByState(state);

        spotifyCredentialValidator.validateOnCallback(spotifyCredential, code);
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code).build();

        try {
            // Get user token
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

            // Save tokens
            spotifyCredential.setToken(authorizationCodeCredentials.getAccessToken());
            spotifyCredential.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
            spotifyCredential.setState(null);

            spotifyCredentialService.save(spotifyCredential);
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new ThirdPartyAPIError("Failed to log in with Spotify: " + e.getMessage());
        }

        return spotifyCredentialMapper.mapCredentialToCredentialResponse(spotifyCredential);
    }

    @PostMapping("/logout")
    public SpotifyCredentialResponse logout(final Authentication auth) {
        User loggedInUser = userService.findByUsername(auth.getName());
        SpotifyCredential spotifyCredential = spotifyCredentialService.findByOwner(loggedInUser);

        spotifyCredentialValidator.validateOnLogout(spotifyCredential);

        loggedInUser.setSpotifyCredential(null);
        userService.save(loggedInUser);
        spotifyCredentialService.delete(spotifyCredential);

        return spotifyCredentialMapper.mapCredentialToCredentialResponse(spotifyCredential);
    }

    @GetMapping("/token")
    public SpotifyCredentialResponse getToken(final Authentication auth) {
        User loggedInUser = userService.findByUsername(auth.getName());
        SpotifyCredential spotifyCredential = spotifyCredentialService.findByOwner(loggedInUser);

        spotifyCredentialValidator.validateOnGetToken(spotifyCredential);

        return spotifyCredentialMapper.mapCredentialToCredentialResponse(spotifyCredential);
    }
}
