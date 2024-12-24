package partydj.backend.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import partydj.backend.rest.entity.SpotifyCredential;
import partydj.backend.rest.entity.User;
import partydj.backend.rest.entity.request.SetSpotifyTokensRequest;
import partydj.backend.rest.entity.response.SpotifyCredentialResponse;
import partydj.backend.rest.entity.response.SpotifyLoginUriResponse;
import partydj.backend.rest.mapper.SpotifyCredentialMapper;
import partydj.backend.rest.repository.SpotifyCredentialRepository;
import partydj.backend.rest.validation.SpotifyCredentialValidator;

import java.util.UUID;

@Service
public class SpotifyCredentialService {

    @Autowired
    private SpotifyCredentialRepository repository;

    @Autowired
    private UserService userService;

    @Autowired
    private SpotifyCredentialValidator validator;

    @Autowired
    private SpotifyService spotifyService;

    @Autowired
    private SpotifyCredentialMapper spotifyCredentialMapper;

    // Repository handlers

    public SpotifyCredential findByOwner(final User owner) {
        final SpotifyCredential spotifyCredential = repository.findByOwner(owner);

        validator.verifyLoggedIn(spotifyCredential);

        return spotifyCredential;
    }

    public SpotifyCredential findByOwnerWithoutExceptionThrowing(final User owner) {
        return repository.findByOwner(owner);
    }

    public SpotifyCredential findByState(final UUID state) {
        final SpotifyCredential spotifyCredential = repository.findByState(state.toString());

        validator.verifyNotNull(spotifyCredential);

        return spotifyCredential;
    }

    public SpotifyCredential save(final SpotifyCredential spotifyCredential) {
        return repository.save(spotifyCredential);
    }

    public SpotifyCredential delete(final SpotifyCredential spotifyCredential) {
        repository.delete(spotifyCredential);

        return spotifyCredential;
    }

    // Controller handlers

    public SpotifyLoginUriResponse getLoginUri(final User loggedInUser) {
        SpotifyCredential spotifyCredential = findByOwnerWithoutExceptionThrowing(loggedInUser);

        validator.verifyNotLoggedIn(spotifyCredential);

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

        return spotifyService.generateLoginUri(state);
    }

    public SpotifyCredentialResponse processCallback(final SetSpotifyTokensRequest request) {
        final SpotifyCredential spotifyCredential = findByState(request.getState());

        return spotifyService.processCallback(spotifyCredential, request.getCode());
    }

    public SpotifyCredentialResponse logout(final User loggedInUser) {
        final SpotifyCredential spotifyCredential = findByOwner(loggedInUser);

        loggedInUser.setSpotifyCredential(null);
        userService.save(loggedInUser);
        delete(spotifyCredential);

        return spotifyCredentialMapper.mapCredentialToCredentialResponse(spotifyCredential);
    }

    public SpotifyCredentialResponse getToken(final User loggedInUser) {
        return spotifyCredentialMapper.mapCredentialToCredentialResponse(
                findByOwner(loggedInUser));
    }

    public SpotifyCredentialResponse refreshToken(final User loggedInUser) {
        final SpotifyCredential spotifyCredential = findByOwner(loggedInUser);

        return spotifyService.refreshToken(spotifyCredential);
    }
}
