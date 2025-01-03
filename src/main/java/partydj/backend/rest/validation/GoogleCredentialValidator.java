package partydj.backend.rest.validation;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;
import partydj.backend.rest.entity.GoogleCredential;

@Component
public class GoogleCredentialValidator {
    public void verifyLoggedIn(final GoogleCredential googleCredential) {
        if (googleCredential == null || googleCredential.getToken() == null) {
            throw new IllegalStateException("You have not connected your Google account.");
        }
    }

    public void verifyNotLoggedIn(final GoogleCredential googleCredential) {
        if (googleCredential != null && googleCredential.getToken() != null) {
            throw new IllegalStateException("You have already connected your Google account.");
        }
    }

    public void verifyNotNull(final GoogleCredential googleCredential) {
        if (googleCredential == null) {
            throw new EntityNotFoundException("Failed to log in with Google. Please try again.");
        }
    }
}
