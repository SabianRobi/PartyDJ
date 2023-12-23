package partydj.backend.rest.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import partydj.backend.rest.domain.*;
import partydj.backend.rest.helper.DataGenerator;
import partydj.backend.rest.repository.PreviousTrackRepository;
import partydj.backend.rest.repository.TrackInQueueRepository;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrackServiceTest {
    @Mock
    private PreviousTrackRepository previousTrackRepository;

    @Mock
    private TrackInQueueRepository trackInQueueRepository;

    @Mock
    private ArtistService artistService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TrackService trackService;

    @Test
    void shouldSaveTrackInQueue() {
        final User user = DataGenerator.generateUserWithId();
        final Party party = DataGenerator.generateParty("", Set.of(user));
        final Artist artist = DataGenerator.generateArtist();
        final TrackInQueue track = DataGenerator.generateTrackInQueue("", party, user, Set.of(artist));
        when(trackInQueueRepository.save(any())).thenReturn(track);

        final TrackInQueue savedTrack = (TrackInQueue) trackService.save(track);

        assertThat(savedTrack).isSameAs(track);
    }

    @Test
    void shouldSavePreviousQueue() {
        final User user = DataGenerator.generateUserWithId();
        final Party party = DataGenerator.generateParty("", Set.of(user));
        final Artist artist = DataGenerator.generateArtist();
        final PreviousTrack track = DataGenerator.generatePreviousTrack("", party, user, Set.of(artist));
        when(previousTrackRepository.save(any())).thenReturn(track);

        final PreviousTrack savedTrack = (PreviousTrack) trackService.save(track);

        assertThat(savedTrack).isSameAs(track);
    }

    @Test
    void shouldDeleteTrackInQueue() {
        final User user = DataGenerator.generateUserWithId();
        final Party party = DataGenerator.generateParty("", Set.of(user));
        final Artist artist = DataGenerator.generateArtist();
        final TrackInQueue track = DataGenerator.generateTrackInQueue("", party, user, Set.of(artist));
        final HashSet<Track> tracks = new HashSet<>(Set.of(track));
        final HashSet<TrackInQueue> tracksInQueue = new HashSet<>(Set.of(track));
        user.setAddedTracks(tracksInQueue);
        artist.setTracks(tracks);
        party.setTracksInQueue(tracksInQueue);
        when(userService.save(any())).thenReturn(user);

        trackService.delete(track);

        assertThat(user.getAddedTracks()).isEmpty();
        assertThat(artist.getTracks()).isEmpty();
        assertThat(party.getTracksInQueue()).isEmpty();
    }

    @Test
    void shouldDeletePreviousTrack() {
        final User user = DataGenerator.generateUserWithId();
        final Party party = DataGenerator.generateParty("", Set.of(user));
        final Artist artist = DataGenerator.generateArtist();
        final PreviousTrack track = DataGenerator.generatePreviousTrack("", party, user, Set.of(artist));
        final HashSet<Track> tracks = new HashSet<>(Set.of(track));
        final HashSet<PreviousTrack> previousTracks = new HashSet<>(Set.of(track));
        artist.setTracks(tracks);
        party.setPreviousTracks(previousTracks);

        trackService.delete(track);

        assertThat(artist.getTracks()).isEmpty();
    }

    @Test
    void givenTrack_whenFindById_thenSuccess() {
        final User user = DataGenerator.generateUserWithId();
        final Party party = DataGenerator.generateParty("", Set.of(user));
        final Artist artist = DataGenerator.generateArtist();
        final TrackInQueue track = DataGenerator.generateTrackInQueue("", party, user, Set.of(artist));
        track.setId(1);
        when(trackInQueueRepository.findById(anyInt())).thenReturn(track);

        final TrackInQueue foundTrack = trackService.findById(track.getId());

        assertThat(foundTrack).isSameAs(track);
    }

    @Test
    void givenTrack_whenFindById_thenThrowsEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> trackService.findById(1));
    }

    @Test
    void givenPopulatedQueue_whenGetNext_thenSuccess() {
        final User user = DataGenerator.generateUserWithId();
        final Party party = DataGenerator.generateParty("", Set.of(user));
        final Artist artist = DataGenerator.generateArtist();
        final TrackInQueue track = DataGenerator.generateTrackInQueue("", party, user, Set.of(artist));
        when(trackInQueueRepository.findTop1ByPartyNameAndIsPlayingIsFalseOrderByScoreDesc(any())).thenReturn(track);

        final TrackInQueue foundTrack = trackService.getNextTrack(party.getName());

        assertThat(foundTrack).isSameAs(track);
    }

    @Test
    void givenEmptyQueue_whenGetNext_thenThrowsEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> trackService.getNextTrack("testParty"));
    }

    @Test
    void givenPopulatedQueue_whenGetNowPlaying_thenSuccess() {
        final User user = DataGenerator.generateUserWithId();
        final Party party = DataGenerator.generateParty("", Set.of(user));
        final Artist artist = DataGenerator.generateArtist();
        final TrackInQueue track = DataGenerator.generateTrackInQueue("", party, user, Set.of(artist));
        when(trackInQueueRepository.findByPartyNameAndIsPlayingIsTrue(any())).thenReturn(track);

        final TrackInQueue foundTrack = trackService.getNowPlaying(party.getName());

        assertThat(foundTrack).isSameAs(track);
    }

    @Test
    void givenEmptyQueue_whenGetNowPlaying_thenThrowsEntityNotFoundException() {
        final TrackInQueue track = trackService.getNowPlaying("testParty");

        assertNull(track);
    }

}