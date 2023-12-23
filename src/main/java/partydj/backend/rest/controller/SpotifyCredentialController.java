package partydj.backend.rest.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import partydj.backend.rest.entity.SpotifyCredential;
import partydj.backend.rest.entity.User;
import partydj.backend.rest.entity.response.SpotifyCredentialResponse;
import partydj.backend.rest.entity.response.SpotifyLoginUriResponse;
import partydj.backend.rest.mapper.SpotifyCredentialMapper;
import partydj.backend.rest.service.SpotifyCredentialService;
import partydj.backend.rest.service.UserService;

@RestController
@RequestMapping(value = "/api/v1/platforms/spotify", produces = "application/json")
public class SpotifyCredentialController {

    @Autowired
    private SpotifyCredentialService spotifyCredentialService;

    @Autowired
    private SpotifyCredentialMapper spotifyCredentialMapper;

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public SpotifyLoginUriResponse getLoginURI(final Authentication auth) {
        final User loggedInUser = userService.findByUsername(auth.getName());

        return new SpotifyLoginUriResponse(spotifyCredentialService.getLoginUri(loggedInUser));
    }

    @GetMapping("/callback")
    @ResponseStatus(HttpStatus.CREATED)
    public SpotifyCredentialResponse processCallback(@RequestParam @NotNull @NotBlank final String code,
                                                     @RequestParam @NotNull @UUID final java.util.UUID state) {
        final SpotifyCredential spotifyCredential = spotifyCredentialService.processCallback(code, state);

        return spotifyCredentialMapper.mapCredentialToCredentialResponse(spotifyCredential);
    }

    @PostMapping("/logout")
    public SpotifyCredentialResponse logout(final Authentication auth) {
        final User loggedInUser = userService.findByUsername(auth.getName());

        final SpotifyCredential spotifyCredential = spotifyCredentialService.logout(loggedInUser);

        return spotifyCredentialMapper.mapCredentialToCredentialResponse(spotifyCredential);
    }

    @GetMapping("/token")
    public SpotifyCredentialResponse getToken(final Authentication auth) {
        final User loggedInUser = userService.findByUsername(auth.getName());

        final SpotifyCredential spotifyCredential = spotifyCredentialService.getToken(loggedInUser);

        return spotifyCredentialMapper.mapCredentialToCredentialResponse(spotifyCredential);
    }

    @PatchMapping("/token")
    public SpotifyCredentialResponse refreshToken(final Authentication auth) {
        final User loggedInUser = userService.findByUsername(auth.getName());

        final SpotifyCredential spotifyCredential = spotifyCredentialService.refreshToken(loggedInUser);

        return spotifyCredentialMapper.mapCredentialToCredentialResponse(spotifyCredential);
    }
}
