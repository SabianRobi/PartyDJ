package partydj.backend.rest.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import partydj.backend.rest.domain.Artist;

@Repository
public interface ArtistsRepository extends CrudRepository<Artist, String> {
}
