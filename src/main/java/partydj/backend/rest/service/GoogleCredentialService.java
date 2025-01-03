package partydj.backend.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import partydj.backend.rest.entity.GoogleCredential;
import partydj.backend.rest.entity.User;
import partydj.backend.rest.entity.request.SetPlatformTokensRequest;
import partydj.backend.rest.entity.response.PlatformCredentialResponse;
import partydj.backend.rest.entity.response.PlatformLoginUriResponse;
import partydj.backend.rest.repository.GoogleCredentialRepository;
import partydj.backend.rest.validation.GoogleCredentialValidator;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class GoogleCredentialService {

    @Autowired
    private GoogleCredentialRepository repository;

    @Autowired
    private GoogleCredentialValidator validator;

    @Autowired
    private UserService userService;

    @Autowired
    private GoogleService googleService;

    // Repository handlers

    public GoogleCredential findByOwner(final User owner) {
        final GoogleCredential googleCredential = repository.findByOwner(owner);

        validator.verifyLoggedIn(googleCredential);

        return googleCredential;
    }

    public GoogleCredential findByOwnerWithoutExceptionThrowing(final User owner) {
        return repository.findByOwner(owner);
    }

    public GoogleCredential findByState(final UUID state) {
        final GoogleCredential googleCredential = repository.findByState(state.toString());

        validator.verifyNotNull(googleCredential);

        return googleCredential;
    }

    public GoogleCredential save(final GoogleCredential googleCredential) {
        return repository.save(googleCredential);
    }

    public GoogleCredential delete(final GoogleCredential googleCredential) {
        repository.delete(googleCredential);

        return googleCredential;
    }

    // Controller handlers

    public PlatformLoginUriResponse getLoginUri(final User loggedInUser) {
        GoogleCredential googleCredential = findByOwnerWithoutExceptionThrowing(loggedInUser);

        validator.verifyNotLoggedIn(googleCredential);

        final String state = UUID.randomUUID().toString();

        if (googleCredential != null) {
            googleCredential.setState(state);
        } else {
            googleCredential = GoogleCredential.builder()
                    .state(state)
                    .owner(loggedInUser)
                    .build();
        }

        save(googleCredential);
        loggedInUser.setGoogleCredential(googleCredential);
        userService.save(loggedInUser);

        return googleService.generateLoginUri(loggedInUser, state);
    }

    public PlatformCredentialResponse processCallback(final SetPlatformTokensRequest request) {
        final String decodedCode = URLDecoder.decode(request.getCode(), StandardCharsets.UTF_8);

        final GoogleCredential googleCredential = findByState(request.getState());

        return googleService.processCallback(googleCredential, decodedCode);
    }

    public PlatformCredentialResponse logout(final User loggedInUser) {
        final GoogleCredential googleCredential = findByOwner(loggedInUser);

        loggedInUser.setGoogleCredential(null);
        userService.save(loggedInUser);
        delete(googleCredential);

        return googleService.revokeTokens(loggedInUser, googleCredential);
    }
}
