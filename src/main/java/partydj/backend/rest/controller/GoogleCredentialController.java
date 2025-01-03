package partydj.backend.rest.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import partydj.backend.rest.entity.User;
import partydj.backend.rest.entity.request.SetPlatformTokensRequest;
import partydj.backend.rest.entity.response.PlatformCredentialResponse;
import partydj.backend.rest.entity.response.PlatformLoginUriResponse;
import partydj.backend.rest.service.GoogleCredentialService;
import partydj.backend.rest.service.UserService;

@RestController
@CrossOrigin
@RequestMapping(value = "/api/v1/platforms/google", produces = "application/json")
public class GoogleCredentialController {

    @Autowired
    private UserService userService;

    @Autowired
    private GoogleCredentialService googleCredentialService;

    @GetMapping("/login")
    public PlatformLoginUriResponse getLoginURI(final Authentication auth) {
        final User loggedInUser = userService.findByUsername(auth.getName());

        return googleCredentialService.getLoginUri(loggedInUser);
    }

    @PostMapping(value = "/callback", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public PlatformCredentialResponse processCallback(@Valid @RequestBody final SetPlatformTokensRequest request) {
        return googleCredentialService.processCallback(request);
    }

    @PostMapping("/logout")
    public PlatformCredentialResponse logout(final Authentication auth) {
        final User loggedInUser = userService.findByUsername(auth.getName());

        return googleCredentialService.logout(loggedInUser);
    }

    @GetMapping("/token")
    public PlatformCredentialResponse getToken(final Authentication auth) {
        final User loggedInUser = userService.findByUsername(auth.getName());

        return googleCredentialService.getToken(loggedInUser);
    }
}
