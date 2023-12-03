package partydj.backend.rest.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import partydj.backend.rest.domain.SpotifyCredential;
import partydj.backend.rest.domain.User;
import partydj.backend.rest.domain.enums.UserType;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class SpotifyCredentialRepositoryTest {
    @Autowired
    private SpotifyCredentialRepository repository;

    @Autowired
    TestEntityManager entityManager;

    private SpotifyCredential spotifyCredential;

    @BeforeEach
    void init() {
        spotifyCredential = SpotifyCredential.builder().owner(null).state("state")
                .token("token").refreshToken("refreshToken").build();
    }

    @Test
    public void givenNewSpotifyCredential_whenSave_thenSuccess() {
        SpotifyCredential savedCredential = repository.save(spotifyCredential);

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
        User user = User.builder()
                .email("us@e.r").username("user").password("password")
                .userType(UserType.NORMAL).addedTracks(new HashSet<>()).build();
        spotifyCredential.setOwner(user);
        entityManager.persist(user);
        entityManager.persist(spotifyCredential);

        SpotifyCredential found = repository.findByOwner(user);

        assertThat(found).isEqualTo(spotifyCredential);
    }

    @Test
    public void givenSpotifyCredential_whenFindByState_thenSuccess() {
        entityManager.persist(spotifyCredential);

        SpotifyCredential found = repository.findByState("state");

        assertThat(found).isEqualTo(spotifyCredential);
    }


}
