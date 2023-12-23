package partydj.backend.rest.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import partydj.backend.rest.entity.Artist;
import partydj.backend.rest.entity.User;
import partydj.backend.rest.helper.DataGenerator;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;

    @BeforeEach
    void init() {
        user = DataGenerator.generateUser("");
    }

    @Test
    public void givenNewUser_whenSave_thenSuccess() {
        User savedUser = repository.save(user);

        assertThat(entityManager.find(User.class, savedUser.getId())).isEqualTo(user);
    }

    @Test
    public void givenUser_whenDelete_thenSuccess() {
        entityManager.persist(user);

        repository.delete(user);

        assertThat(entityManager.find(Artist.class, user.getId())).isNull();
    }

    @Test
    public void givenUser_whenFindByUsername_thenSuccess() {
        entityManager.persist(user);

        User foundUser = repository.findByUsername(user.getUsername());

        assertThat(foundUser).isNotNull().isEqualTo(user);
    }
}
