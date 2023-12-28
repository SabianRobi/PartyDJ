package partydj.backend.rest.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import partydj.backend.rest.entity.Party;

@Repository
public interface PartyRepository extends CrudRepository<Party, Integer> {
    Party findByName(final String name);
}
