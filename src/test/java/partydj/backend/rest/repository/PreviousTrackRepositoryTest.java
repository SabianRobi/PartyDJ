package partydj.backend.rest.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import partydj.backend.rest.entity.PreviousTrack;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PreviousTrackRepositoryTest {
    @Autowired
    private PreviousTrackRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private PreviousTrack track;

    @BeforeEach
    void init() {
        track = PreviousTrack.builder()
                .title("title")
                .uri("uri")
                .coverUri("coverUri")
                .endedAt(LocalDateTime.now())
                .build();
    }

    @Test
    public void givenNewPreviousTrack_whenSave_thenSuccess() {
        PreviousTrack savedTrack = repository.save(track);
        // TODO: Why doesn't it throw any exception, uri format clearly wrong

        assertThat(entityManager.find(PreviousTrack.class, savedTrack.getId())).isEqualTo(track);
    }

    @Test
    public void givenPreviousTrack_whenDelete_thenSuccess() {
        entityManager.persist(track);

        repository.delete(track);

        assertThat(entityManager.find(PreviousTrack.class, track.getId())).isNull();
    }

}
