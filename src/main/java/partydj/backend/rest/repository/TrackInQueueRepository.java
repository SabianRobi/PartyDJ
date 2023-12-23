package partydj.backend.rest.repository;

import org.springframework.data.repository.CrudRepository;
import partydj.backend.rest.entity.TrackInQueue;

public interface TrackInQueueRepository extends CrudRepository<TrackInQueue, Integer> {
    TrackInQueue findById(final int id);

    // Get next track
    TrackInQueue findTop1ByPartyNameAndIsPlayingIsFalseOrderByScoreDesc(final String partyName);

    // Get now playing track
    TrackInQueue findByPartyNameAndIsPlayingIsTrue(final String partyName);
}