package partydj.backend.rest.repository;

import org.springframework.data.repository.CrudRepository;
import partydj.backend.rest.domain.Party;

public interface PartyRepository extends CrudRepository<Party, Integer> {
    boolean existsByName(final String name);

    Party findByName(final String name);
}
