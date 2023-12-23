package partydj.backend.rest.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import partydj.backend.rest.entity.Artist;
import partydj.backend.rest.entity.Track;

import java.util.HashSet;
import java.util.List;

@Repository
public interface ArtistRepository extends CrudRepository<Artist, String> {
    HashSet<Artist> findAllByTracksContaining(final Track track);

    HashSet<Artist> findAllByNameIn(final List<String> artistNames);
}
