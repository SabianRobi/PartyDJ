package partydj.backend.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import partydj.backend.rest.domain.TrackInQueue;
import partydj.backend.rest.repository.TrackRepository;

@Service
public class TrackService {
    @Autowired
    private TrackRepository repository;

    public TrackInQueue save(final TrackInQueue trackInQueue) {
        return repository.save(trackInQueue);
    }

    public void delete(final TrackInQueue trackInQueue) {
        repository.delete(trackInQueue);
    }

    public TrackInQueue findById(final int trackId) {
        return repository.findById(trackId);
    }

    public TrackInQueue getNextTrack(final String partyName) {
        return repository.findTop1ByPartyNameAndIsPlayingIsFalseOrderByScoreDesc(partyName);
    }

    public TrackInQueue getNowPlaying(final String partyName) {
        return repository.findByPartyNameAndIsPlayingIsTrue(partyName);
    }
}
