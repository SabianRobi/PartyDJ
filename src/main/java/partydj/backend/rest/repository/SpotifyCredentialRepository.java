package partydj.backend.rest.repository;

import org.springframework.data.repository.CrudRepository;
import partydj.backend.rest.domain.SpotifyCredential;

public interface SpotifyCredentialRepository extends CrudRepository<SpotifyCredential, Integer> {
}
