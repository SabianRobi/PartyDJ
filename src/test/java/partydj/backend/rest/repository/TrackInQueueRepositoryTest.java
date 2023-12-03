package partydj.backend.rest.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import partydj.backend.rest.domain.Party;
import partydj.backend.rest.domain.TrackInQueue;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class TrackInQueueRepositoryTest {
    @Autowired
    private TrackInQueueRepository repository;

    @Autowired
    TestEntityManager entityManager;

    private TrackInQueue track;

    @BeforeEach
    void init() {
        track = TrackInQueue.builder().uri("uri").title("title").coverUri("coverUri").length(0).score(0).build();
    }

    @Test
    public void givenNewTrackInQueue_whenSave_thenSuccess() {
        TrackInQueue savedTrack = repository.save(track);

        assertThat(entityManager.find(TrackInQueue.class, savedTrack.getId())).isEqualTo(track);
    }

    @Test
    public void givenTrackInQueue_whenDelete_thenSuccess() {
        entityManager.persist(track);

        repository.delete(track);

        assertThat(entityManager.find(TrackInQueue.class, track.getId())).isNull();
    }

    @Test
    public void givenTrackInQueue_whenFindById_thenSuccess() {
        entityManager.persist(track);

        TrackInQueue found = repository.findById(track.getId());

        assertThat(found).isEqualTo(track);
    }

    @Test
    public void givenTrackInQueues_whenGetNextTrack_thenSuccess() {
        Party party = Party.builder().name("party").tracksInQueue(new HashSet<>()).build();
        entityManager.persist(party);
        track.setParty(party);
        TrackInQueue track1 = TrackInQueue.builder()
                .party(party)
                .isPlaying(false)
                .uri("uri").title("title").coverUri("coverUri").length(0).score(10).build();
        entityManager.persist(track);
        entityManager.persist(track1);

        TrackInQueue nextTrack = repository.findTop1ByPartyNameAndIsPlayingIsFalseOrderByScoreDesc("party");

        assertThat(nextTrack).isEqualTo(track1);
    }

    @Test
    public void givenTrackInQueues_whenGetNowPlayingTrack_thenSuccess() {
        Party party = Party.builder().name("party").tracksInQueue(new HashSet<>()).build();
        entityManager.persist(party);
        track.setParty(party);
        track.setPlaying(true);
        entityManager.persist(track);

        TrackInQueue nowPlayingTrack = repository.findByPartyNameAndIsPlayingIsTrue("party");

        assertThat(nowPlayingTrack).isEqualTo(track);
    }

}
