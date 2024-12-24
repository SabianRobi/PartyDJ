package partydj.backend.rest.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import partydj.backend.rest.entity.User;
import partydj.backend.rest.entity.request.SetSpotifyTokensRequest;
import partydj.backend.rest.entity.response.SpotifyCredentialResponse;
import partydj.backend.rest.entity.response.SpotifyLoginUriResponse;
import partydj.backend.rest.service.SpotifyCredentialService;
import partydj.backend.rest.service.UserService;

@RestController
@CrossOrigin
@RequestMapping(value = "/api/v1/platforms/spotify", produces = "application/json")
public class SpotifyCredentialController {

    @Autowired
    private SpotifyCredentialService spotifyCredentialService;

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public SpotifyLoginUriResponse getLoginURI(final Authentication auth) {
        final User loggedInUser = userService.findByUsername(auth.getName());

        return spotifyCredentialService.getLoginUri(loggedInUser);
    }

    @PostMapping(value = "/callback", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public SpotifyCredentialResponse processCallback(@Valid @RequestBody final SetSpotifyTokensRequest request) {
        return spotifyCredentialService.processCallback(request);
    }

    @PostMapping("/logout")
    public SpotifyCredentialResponse logout(final Authentication auth) {
        final User loggedInUser = userService.findByUsername(auth.getName());

        return spotifyCredentialService.logout(loggedInUser);
    }

    @GetMapping("/token")
    public SpotifyCredentialResponse getToken(final Authentication auth) {
        final User loggedInUser = userService.findByUsername(auth.getName());

        return spotifyCredentialService.getToken(loggedInUser);
    }

    @PatchMapping("/token")
    public SpotifyCredentialResponse refreshToken(final Authentication auth) {
        final User loggedInUser = userService.findByUsername(auth.getName());

        return spotifyCredentialService.refreshToken(loggedInUser);
    }
}
