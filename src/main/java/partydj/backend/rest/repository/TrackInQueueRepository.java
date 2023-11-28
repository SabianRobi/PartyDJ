package partydj.backend.rest.repository;

import org.springframework.data.repository.CrudRepository;
import partydj.backend.rest.domain.TrackInQueue;

public interface TrackInQueueRepository extends CrudRepository<TrackInQueue, Integer> {
    TrackInQueue findById(final int id);

    TrackInQueue findTop1ByPartyNameAndIsPlayingIsFalseOrderByScoreDesc(final String partyName);

    TrackInQueue findByPartyNameAndIsPlayingIsTrue(final String partyName);
}