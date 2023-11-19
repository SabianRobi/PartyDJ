package partydj.backend.rest.repository;

import org.springframework.data.repository.CrudRepository;
import partydj.backend.rest.domain.SpotifyCredential;
import partydj.backend.rest.domain.User;

public interface SpotifyCredentialRepository extends CrudRepository<SpotifyCredential, Integer> {
    SpotifyCredential findById(final int id);

    SpotifyCredential findByState(final String state);

    SpotifyCredential findByOwner(final User owner);
}
