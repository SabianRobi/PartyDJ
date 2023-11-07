package partydj.backend.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import partydj.backend.rest.domain.Track;
import partydj.backend.rest.repository.TrackRepository;

@Service
public class TrackService {
    @Autowired
    private TrackRepository repository;

    public Track save(final Track track) {
        return repository.save(track);
    }

    public void delete(final Track track) {
        repository.delete(track);
    }

    public Track findById(final int trackId) {
        return repository.findById(trackId);
    }
}
