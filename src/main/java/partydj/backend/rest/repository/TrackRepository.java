package partydj.backend.rest.repository;

import org.springframework.data.repository.CrudRepository;
import partydj.backend.rest.domain.Track;

public interface TrackRepository extends CrudRepository<Track, Integer> {
    Track findById(final int id);

    Track findTop1ByPartyNameAndIsPlayingIsFalseOrderByScoreDesc(final String partyName);

    Track findByPartyNameAndIsPlayingIsTrue(final String partyName);
}