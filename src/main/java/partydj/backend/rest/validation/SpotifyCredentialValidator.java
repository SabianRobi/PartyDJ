package partydj.backend.rest.validation;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;
import partydj.backend.rest.entity.SpotifyCredential;

@Component
public class SpotifyCredentialValidator {
    public void verifyLoggedIn(final SpotifyCredential spotifyCredential) {
        if (spotifyCredential == null || spotifyCredential.getToken() == null) {
            throw new IllegalStateException("You have not connected your Spotify account.");
        }
    }

    public void verifyNotLoggedIn(final SpotifyCredential spotifyCredential) {
        if (spotifyCredential != null && spotifyCredential.getToken() != null) {
            throw new IllegalStateException("You have already connected your Spotify account.");
        }
    }

    public void verifyNotNull(final SpotifyCredential spotifyCredential) {
        if (spotifyCredential == null) {
            throw new EntityNotFoundException("Failed to log in with Spotify. Please try again.");
        }
    }


}
