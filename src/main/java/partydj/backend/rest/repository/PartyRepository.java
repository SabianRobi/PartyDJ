package partydj.backend.rest.repository;

import org.springframework.data.repository.CrudRepository;
import partydj.backend.rest.entity.Party;

public interface PartyRepository extends CrudRepository<Party, Integer> {
    Party findByName(final String name);
}
