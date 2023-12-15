package partydj.backend.rest.validation;

import org.springframework.stereotype.Component;
import partydj.backend.rest.domain.SpotifyCredential;

@Component
public class SpotifyCredentialValidator {
    public void verifyLoggedIn(final SpotifyCredential spotifyCredential) {
        if (spotifyCredential.getToken() == null) {
            throw new IllegalStateException("You have not connected your Spotify account.");
        }
    }
}
