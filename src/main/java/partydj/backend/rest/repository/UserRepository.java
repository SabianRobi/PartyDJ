package partydj.backend.rest.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import partydj.backend.rest.domain.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    User findById(final int id);

    boolean existsByUsername(final String username);

    boolean existsByEmail(final String email);
}
