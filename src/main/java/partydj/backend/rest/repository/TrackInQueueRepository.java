package partydj.backend.rest.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import partydj.backend.rest.entity.TrackInQueue;

@Repository
public interface TrackInQueueRepository extends CrudRepository<TrackInQueue, Integer> {
    TrackInQueue findById(final int id);

    // Get next track
    TrackInQueue findTop1ByPartyNameAndIsPlayingIsFalseOrderByScoreDesc(final String partyName);

    // Get now playing track
    TrackInQueue findByPartyNameAndIsPlayingIsTrue(final String partyName);
}