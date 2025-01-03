package partydj.backend.rest.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import partydj.backend.rest.entity.GoogleCredential;
import partydj.backend.rest.entity.User;

@Repository
public interface GoogleCredentialRepository extends CrudRepository<GoogleCredential, Integer> {
    GoogleCredential findByState(final String state);

    GoogleCredential findByOwner(final User owner);
}
