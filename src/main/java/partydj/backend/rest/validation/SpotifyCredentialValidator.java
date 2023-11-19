package partydj.backend.rest.validation;

import org.springframework.stereotype.Service;
import partydj.backend.rest.domain.SpotifyCredential;
import partydj.backend.rest.domain.error.RequiredFieldMissingException;

@Service
public class SpotifyCredentialValidator {

    public void validateOnGetLoginURI(final SpotifyCredential spotifyCredential) {
        if (spotifyCredential != null && spotifyCredential.getState() == null) {
            throw new IllegalStateException("You have already connected your Spotify account.");
        }
    }

    public void validateOnCallback(final SpotifyCredential spotifyCredential, final String code) {
        if (spotifyCredential == null) {
            throw new IllegalStateException("Login failed. Please try again.");
        }

        if (code.isBlank()) {
            throw new RequiredFieldMissingException("Login failed. Please try again.");
        }
    }

    public void validateOnLogout(final SpotifyCredential spotifyCredential) {
        VerifyCredentialNotNull(spotifyCredential);
    }

    public void validateOnGetToken(final SpotifyCredential spotifyCredential) {
        VerifyCredentialNotNull(spotifyCredential);

        if (spotifyCredential.getToken() == null) {
            throw new IllegalStateException("You aren't logged in with Spotify.");
        }
    }

    private void VerifyCredentialNotNull(final SpotifyCredential spotifyCredential) {
        if (spotifyCredential == null) {
            throw new IllegalStateException("You aren't logged in with Spotify.");
        }
    }
}
