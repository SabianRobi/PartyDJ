package partydj.backend.rest.repository;

import org.springframework.data.repository.CrudRepository;
import partydj.backend.rest.domain.User;

public interface UserRepository extends CrudRepository<User, Integer> {
}
