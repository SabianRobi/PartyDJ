package partydj.backend.rest.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import partydj.backend.rest.entity.SpotifyCredential;
import partydj.backend.rest.entity.User;

import static org.assertj.core.api.Assertions.assertThat;
import static partydj.backend.rest.helper.DataGenerator.generateSpotifyCredentialWithOnlyState;
import static partydj.backend.rest.helper.DataGenerator.generateUserWithoutId;

@DataJpaTest
public class SpotifyCredentialRepositoryTest {
    @Autowired
    private SpotifyCredentialRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private SpotifyCredential spotifyCredential;
    private User user;

    @BeforeEach
    void init() {
        user = entityManager.persist(generateUserWithoutId(""));
        spotifyCredential = generateSpotifyCredentialWithOnlyState(user);
    }

    @Test
    public void givenNewSpotifyCredential_whenSave_thenSuccess() {
        final SpotifyCredential savedCredential = repository.save(spotifyCredential);

        assertThat(entityManager.find(SpotifyCredential.class, savedCredential.getId())).isEqualTo(spotifyCredential);
    }

    @Test
    public void givenSpotifyCredential_whenDelete_thenSuccess() {
        entityManager.persist(spotifyCredential);

        repository.delete(spotifyCredential);

        assertThat(entityManager.find(SpotifyCredential.class, spotifyCredential.getId())).isNull();
    }

    @Test
    public void givenSpotifyCredential_whenFindByOwner_thenSuccess() {
        entityManager.persist(spotifyCredential);

        final SpotifyCredential found = repository.findByOwner(user);

        assertThat(found).isEqualTo(spotifyCredential);
    }

    @Test
    public void givenSpotifyCredential_whenFindByState_thenSuccess() {
        entityManager.persist(spotifyCredential);

        final SpotifyCredential found = repository.findByState(spotifyCredential.getState());

        assertThat(found).isEqualTo(spotifyCredential);
    }
}
