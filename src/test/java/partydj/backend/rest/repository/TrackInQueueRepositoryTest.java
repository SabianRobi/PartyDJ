package partydj.backend.rest.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import partydj.backend.rest.entity.Artist;
import partydj.backend.rest.entity.Party;
import partydj.backend.rest.entity.TrackInQueue;
import partydj.backend.rest.entity.User;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static partydj.backend.rest.helper.DataGenerator.*;

@DataJpaTest
public class TrackInQueueRepositoryTest {
    @Autowired
    private TrackInQueueRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;
    private Party party;
    private Artist artist;
    private TrackInQueue track;

    @BeforeEach
    public void init() {
        user = entityManager.persist(generateUser(""));
        artist = entityManager.persist(generateArtist());
        party = entityManager.persist(generateParty("", Set.of(user)));
        track = generateTrackInQueue("", party, user, Set.of(artist));
    }

    @Test
    public void givenNewTrackInQueue_whenSave_thenSuccess() {
        final TrackInQueue savedTrack = repository.save(track);

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

        final TrackInQueue found = repository.findById(track.getId());

        assertThat(found).isEqualTo(track);
    }

    @Test
    public void givenTrackInQueues_whenGetNextTrack_thenSuccess() {
        entityManager.persist(track);
        final TrackInQueue track1 = generateTrackInQueue("2", party, user, Set.of(artist));
        track1.setScore(10);
        entityManager.persist(track1);

        final TrackInQueue nextTrack = repository.findTop1ByPartyNameAndIsPlayingIsFalseOrderByScoreDesc("party");

        assertThat(nextTrack).isEqualTo(track1);
    }

    @Test
    public void givenTrackInQueues_whenGetNowPlayingTrack_thenSuccess() {
        track.setPlaying(true);
        entityManager.persist(track);

        final TrackInQueue nowPlayingTrack = repository.findByPartyNameAndIsPlayingIsTrue("party");

        assertThat(nowPlayingTrack).isEqualTo(track);
    }

}
