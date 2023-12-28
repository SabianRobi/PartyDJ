package partydj.backend.rest.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import partydj.backend.rest.entity.SpotifyCredential;
import partydj.backend.rest.entity.User;

@Repository
public interface SpotifyCredentialRepository extends CrudRepository<SpotifyCredential, Integer> {
    SpotifyCredential findByState(final String state);

    SpotifyCredential findByOwner(final User owner);
}
