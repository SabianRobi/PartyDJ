package partydj.backend.rest.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import partydj.backend.rest.domain.PreviousTrack;

@Repository
public interface PreviousTrackRepository extends CrudRepository<PreviousTrack, String> {
}
