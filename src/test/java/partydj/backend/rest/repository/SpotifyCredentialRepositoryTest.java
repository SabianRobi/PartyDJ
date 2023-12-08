package partydj.backend.rest.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import partydj.backend.rest.domain.SpotifyCredential;
import partydj.backend.rest.domain.User;

import static org.assertj.core.api.Assertions.assertThat;
import static partydj.backend.rest.helper.DataGenerator.generateSpotifyCredential;
import static partydj.backend.rest.helper.DataGenerator.generateUser;

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
        user = entityManager.persist(generateUser(""));
        spotifyCredential = generateSpotifyCredential(user);
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

        final SpotifyCredential found = repository.findByState("1593bead-e671-4a0b-a195-b5165aed6410");

        assertThat(found).isEqualTo(spotifyCredential);
    }
}
