package partydj.backend.rest.validation;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;
import partydj.backend.rest.entity.TrackInQueue;

@Component
public class TrackValidator {

    public void verifyNotNull(final TrackInQueue track) {
        verifyNotNull(track, "Track does not exists.");
    }

    public void verifyNotNull(final TrackInQueue track, final String errorMessage) {
        if (track == null) {
            throw new EntityNotFoundException(errorMessage);
        }
    }
}
