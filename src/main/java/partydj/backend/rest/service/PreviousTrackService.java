package partydj.backend.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import partydj.backend.rest.domain.PreviousTrack;
import partydj.backend.rest.repository.PreviousTrackRepository;

@Service
public class PreviousTrackService {

    @Autowired
    private PreviousTrackRepository repository;

    public PreviousTrack save(final PreviousTrack track) {
        return repository.save(track);
    }
}
