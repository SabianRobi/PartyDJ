package partydj.backend.rest.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import partydj.backend.rest.entity.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    User findByUsername(final String username);
}
