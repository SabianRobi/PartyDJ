package partydj.backend.rest.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import partydj.backend.rest.domain.*;
import partydj.backend.rest.domain.enums.PartyRole;
import partydj.backend.rest.domain.enums.PlatformType;
import partydj.backend.rest.domain.error.NotUniqueException;
import partydj.backend.rest.domain.request.AddTrackRequest;
import partydj.backend.rest.domain.request.PartyRequest;
import partydj.backend.rest.domain.request.SetSpotifyDeviceIdRequest;
import partydj.backend.rest.domain.response.TrackSearchResultResponse;
import partydj.backend.rest.helper.DataGenerator;
import partydj.backend.rest.mapper.TrackMapper;
import partydj.backend.rest.repository.PartyRepository;
import partydj.backend.rest.validation.PartyValidator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PartyServiceTest {
    @Mock
    private PartyRepository partyRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserService userService;

    @Mock
    private TrackService trackService;

    @Mock
    private PartyValidator validator;

    @Mock
    private SpotifyService spotifyService;

    @Mock
    private TrackMapper trackMapper;

    @Mock
    private ArtistService artistService;

    @InjectMocks
    private PartyService partyService;

    @Test
    void shouldRegister() {
        final PartyRequest partyRequest = DataGenerator.generatePartyRequest();
        final User user = DataGenerator.generateUserWithId();
        final Party party = DataGenerator.generateParty("", Set.of(user));
        when(partyRepository.save(any())).thenReturn(party);

        final Party registeredParty = partyService.register(partyRequest, user);

        assertThat(registeredParty).isSameAs(party);
        assertThat(registeredParty.getParticipants()).contains(user);
    }

    @Test
    void givenParty_whenSave_thenSuccess() {
        final User user = DataGenerator.generateUserWithId();
        final Party party = DataGenerator.generateParty("", Set.of(user));
        when(partyRepository.save(any())).thenReturn(party);

        final Party savedParty = partyService.save(party);

        assertThat(savedParty).isSameAs(party);
    }

    @Test
    void givenParty_whenDeleteByName_thenSuccess() {
        final User user = DataGenerator.generateUserWithId();
        final Party party = DataGenerator.generateParty("", Set.of(user));
        when(partyRepository.findByName(any())).thenReturn(party);
        when(userService.saveAll(any())).thenReturn(Set.of(user));

        final Party deletedParty = partyService.deleteByName(user, party.getName());

        assertThat(deletedParty).isSameAs(party);
        assertThat(user.getParty()).isNull();
        assertThat(user.getPartyRole()).isNull();
    }

    @Test
    void givenParty_whenFindByName_thenSuccess() {
        final User user = DataGenerator.generateUserWithId();
        final Party party = DataGenerator.generateParty("", Set.of(user));
        when(partyRepository.findByName(any())).thenReturn(party);

        final Party foundParty = partyService.findByName(party.getName());

        assertThat(foundParty).isSameAs(party);
    }

    @Test
    void givenParty_whenFindByName_thenThrowsEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> partyService.findByName("testParty"));
    }

    @Test
    void givenPartyWithNameAlreadyUsed_whenTryToRegister_thenThrowsNotUniqueException() {
        final User user = DataGenerator.generateUserWithId();
        final Party party = DataGenerator.generateParty("", Set.of(user));
        final PartyRequest partyRequest = DataGenerator.generatePartyRequest();
        final String message = "... Duplicate entry '" + partyRequest.getName() + "' for key ...";
        when(partyRepository.save(any())).thenThrow(new DataIntegrityViolationException(message));

        assertThrows(NotUniqueException.class, () -> partyService.tryToSave(party, partyRequest));
    }

    @Test
    void givenPartyWithNameAlreadyUsed_whenTryToSave_thenThrowsNotUniqueException() {
        final User user = DataGenerator.generateUserWithId();
        final Party party = DataGenerator.generateParty("", Set.of(user));
        final String message = "... Duplicate entry '" + party.getName() + "' for key ...";
        when(partyRepository.save(any())).thenThrow(new DataIntegrityViolationException(message));

        assertThrows(NotUniqueException.class, () -> partyService.tryToSave(party, null));
    }

    @Test
    void givenPartyWithNameAlreadyUsed_whenTryToSave_thenThrowsException() {
        final User user = DataGenerator.generateUserWithId();
        final Party party = DataGenerator.generateParty("", Set.of(user));
        when(partyRepository.save(any())).thenThrow(new DataIntegrityViolationException("Unknown error."));

        assertThrows(IllegalStateException.class, () -> partyService.tryToSave(party, null));
    }

    @Test
    void shouldCreateParty() {
        final User user = DataGenerator.generateUserWithId();
        final Party party = DataGenerator.generateParty("", Set.of(user));
        final PartyRequest partyRequest = DataGenerator.generatePartyRequest();
        when(partyRepository.save(any())).thenReturn(party);
        when(userService.save(any())).thenReturn(user);

        final Party createdParty = partyService.create(user, partyRequest);

        assertThat(createdParty.getParticipants()).contains(user);
        assertThat(user.getParty()).isSameAs(createdParty);
        assertThat(user.getPartyRole()).isSameAs(PartyRole.CREATOR);
    }

    @Test
    void shouldLoadParty() {
        final User user = DataGenerator.generateUserWithId();
        final Party party = DataGenerator.generateParty("", Set.of(user));
        user.setParty(party);
        user.setPartyRole(PartyRole.CREATOR);
        when(partyRepository.findByName(any())).thenReturn(party);

        final Party foundParty = partyService.load(user, party.getName());

        assertThat(foundParty).isSameAs(party);
    }

    @Test
    void givenUserInOtherParty_whenLoad_thenThrowsException() {
        final User user = DataGenerator.generateUser("1");
        final Party party = DataGenerator.generateParty("", Set.of(user));
        user.setParty(party);
        user.setPartyRole(PartyRole.CREATOR);

        assertThrows(IllegalStateException.class, () -> partyService.load(user, "otherPartyName"));
    }

    @Test
    void givenUserNotInParty_whenLoad_thenThrowsException() {
        final User user = DataGenerator.generateUserWithId();

        assertThrows(IllegalStateException.class, () -> partyService.load(user, "partyName"));
    }

    @Test
    void givenPartyAndUser_whenJoinParty_thenSuccess() {
        final User user1 = DataGenerator.generateUser("1");
        final User user2 = DataGenerator.generateUser("2");
        final Party party = DataGenerator.generateParty("", Set.of(user1));
        final PartyRequest partyRequest = DataGenerator.generatePartyRequest();
        when(partyRepository.findByName(any())).thenReturn(party);
        when(userService.save(any())).thenReturn(user2);

        final Party joinedParty = partyService.join(user2, partyRequest);

        assertThat(joinedParty).isSameAs(party);
        assertThat(user2.getParty()).isSameAs(party);
        assertThat(user2.getPartyRole()).isEqualTo(PartyRole.PARTICIPANT);
        assertThat(joinedParty.getParticipants()).hasSize(2);
    }

    @Test
    void givenParty_whenLeave_thenSuccess() {
        final User user = DataGenerator.generateUserWithId();
        user.setPartyRole(PartyRole.CREATOR);
        final Party party = DataGenerator.generateParty("", Set.of(user));
        user.setParty(party);
        when(partyRepository.findByName(any())).thenReturn(party);
        when(partyRepository.save(any())).thenReturn(party);
        when(userService.save(any())).thenReturn(user);

        final Party leftParty = partyService.leave(user, party.getName());

        assertThat(leftParty).isSameAs(party);
        assertThat(user.getPartyRole()).isNull();
        assertThat(user.getParty()).isNull();
        assertThat(party.getParticipants()).doesNotContain(user);
    }

    @Test
    void givenSearchQuery_whenSearch_thenSuccess() {
        final User user = DataGenerator.generateUserWithId();
        final Party party = DataGenerator.generateParty("", Set.of(user));
        final Artist artist = DataGenerator.generateArtist();
        final String query = "query";
        final int offset = 0;
        final int limit = 1;
        final HashSet<PlatformType> platforms = new HashSet<>(Set.of(PlatformType.SPOTIFY));
        final HashSet<TrackSearchResultResponse> response = new HashSet<>();
        final TrackSearchResultResponse trackResponse = DataGenerator.generateTrackSearchResultResponse(Set.of(artist));
        response.add(trackResponse);
        when(partyRepository.findByName(any())).thenReturn(party);
        when(spotifyService.search(any(User.class), any(String.class), any(Integer.class), any(Integer.class)))
                .thenReturn(response);

        final Collection<TrackSearchResultResponse> result =
                partyService.search(user, party.getName(), query, offset, limit, platforms);

        assertThat(result).contains(trackResponse);
    }

    @Test
    void givenParty_whenAddTrack_thenSuccess() {
        final User user = DataGenerator.generateUserWithId();
        final Party party = DataGenerator.generateParty("", Set.of(user));
        final Artist artist = DataGenerator.generateArtist();
        final AddTrackRequest trackRequest = DataGenerator.generateAddTrackRequest();
        final TrackInQueue track = DataGenerator.generateTrackInQueue("", party, user, Set.of(artist));
        when(partyRepository.findByName(any())).thenReturn(party);
        when(spotifyService.fetchAndSafeTrackInfo(any(), any(), any())).thenReturn(track);
        when(partyRepository.save(any())).thenReturn(party);
        when(userService.save(any())).thenReturn(user);

        final TrackInQueue addedTrack = partyService.addTrack(user, trackRequest, party.getName());

        assertThat(addedTrack).isIn(party.getTracksInQueue());
        assertThat(addedTrack).isIn(user.getAddedTracks());
    }

    @Test
    void givenTracksInQueue_whenGetTracks_thenSuccess() {
        final User user = DataGenerator.generateUserWithId();
        final Party party = DataGenerator.generateParty("", Set.of(user));
        final Artist artist = DataGenerator.generateArtist();
        final TrackInQueue track = DataGenerator.generateTrackInQueue("", party, user, Set.of(artist));
        party.setTracksInQueue(Set.of(track));
        when(partyRepository.findByName(any())).thenReturn(party);

        final Set<TrackInQueue> foundTracks = partyService.getTracks(user, party.getName());

        assertThat(foundTracks).contains(track);
    }

    @Test
    void givenPreviousTracks_whenGetPreviousTracks_thenSuccess() {
        final User user = DataGenerator.generateUserWithId();
        final Party party = DataGenerator.generateParty("", Set.of(user));
        final Artist artist = DataGenerator.generateArtist();
        final PreviousTrack track = DataGenerator.generatePreviousTrack("", party, user, Set.of(artist));
        party.setPreviousTracks(Set.of(track));
        when(partyRepository.findByName(any())).thenReturn(party);

        final Set<PreviousTrack> foundTracks = partyService.getPreviousTracks(user, party.getName());

        assertThat(foundTracks).contains(track);
    }

    @Test
    void givenParty_whenSetSpotifyDeviceId_thenSuccess() {
        final User user = DataGenerator.generateUserWithId();
        final Party party = DataGenerator.generateParty("", Set.of(user));
        final SetSpotifyDeviceIdRequest spotifyRequest = DataGenerator.generateSpotifyRequest();
        when(partyRepository.findByName(any())).thenReturn(party);
        when(partyRepository.save(any())).thenReturn(party);

        final Party updatedParty = partyService.setSpotifyDeviceId(user, spotifyRequest, party.getName());

        assertThat(updatedParty).isSameAs(party);
        assertThat(updatedParty.getSpotifyDeviceId()).isEqualTo(spotifyRequest.getDeviceId());
    }

    @Test
    void givenTrackInQueue_whenRemoveTrackFromQueue_thenSuccess() {
        final User user = DataGenerator.generateUserWithId();
        final Party party = DataGenerator.generateParty("", Set.of(user));
        final Artist artist = DataGenerator.generateArtist();
        final TrackInQueue track = DataGenerator.generateTrackInQueueWithId("", party, user, Set.of(artist));
        final HashSet<TrackInQueue> tracks = new HashSet<>(Set.of(track));
        party.setTracksInQueue(tracks);
        user.setAddedTracks(tracks);
        when(partyRepository.findByName(any())).thenReturn(party);
        when(trackService.findById(anyInt())).thenReturn(track);
        when(partyRepository.save(any())).thenReturn(party);

        partyService.removeTrackFromQueue(user, party.getName(), track.getId());

        assertThat(party.getTracksInQueue()).isEmpty();
        assertThat(user.getAddedTracks()).isEmpty();
    }

    @Test
    void givenNowPlayingTrackAndNextTrack_whenPlayNextTrack_thenSuccess() {
        final User user = DataGenerator.generateUserWithId();
        final Party party = DataGenerator.generateParty("", Set.of(user));
        final Artist artist = DataGenerator.generateArtist();
        final TrackInQueue nowPlayingTrack = DataGenerator.generateTrackInQueueWithId("now", party, user, Set.of(artist));
        nowPlayingTrack.setPlaying(true);
        final TrackInQueue nextTrack = DataGenerator.generateTrackInQueueWithId("next", party, user, Set.of(artist));
        nextTrack.setScore(10);
        final HashSet<TrackInQueue> tracks = new HashSet<>(Set.of(nowPlayingTrack, nextTrack));
        party.setTracksInQueue(tracks);
        user.setAddedTracks(tracks);
        final PreviousTrack prevTrack = DataGenerator.generatePreviousTrack("", party, user, Set.of(artist));

        when(partyRepository.findByName(any())).thenReturn(party);
        when(trackService.getNowPlaying(any())).thenReturn(nowPlayingTrack);
        when(trackService.getNextTrack(any())).thenReturn(nextTrack);
        when(trackMapper.mapTrackInQueueToPreviousTrack(any())).thenReturn(prevTrack);
        when(trackService.save(any())).thenReturn(nextTrack);

        partyService.playNextTrack(user, party.getName());

        assertThat(party.getPreviousTracks()).contains(prevTrack);
        assertThat(party.getTracksInQueue()).hasSize(1).contains(nextTrack);
        assertThat(user.getAddedTracks()).hasSize(1).doesNotContain(nowPlayingTrack);
        assertThat(nextTrack.isPlaying()).isTrue();
    }
}
