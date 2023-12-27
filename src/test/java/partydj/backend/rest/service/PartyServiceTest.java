package partydj.backend.rest.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import partydj.backend.rest.entity.*;
import partydj.backend.rest.entity.enums.PartyRole;
import partydj.backend.rest.entity.enums.PlatformType;
import partydj.backend.rest.entity.error.NotUniqueException;
import partydj.backend.rest.entity.request.AddTrackRequest;
import partydj.backend.rest.entity.request.PartyRequest;
import partydj.backend.rest.entity.request.SetSpotifyDeviceIdRequest;
import partydj.backend.rest.entity.response.*;
import partydj.backend.rest.helper.DataGenerator;
import partydj.backend.rest.mapper.PartyMapper;
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
import static org.mockito.Mockito.*;

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

    @Mock
    private PartyMapper partyMapper;

    @InjectMocks
    private PartyService partyService;

    final private User user;
    final private Artist artist;
    final private Party party;
    final private PreviousTrack previousTrack;
    final private TrackInQueue trackInQueue;
    final private ArtistResponse artistResponse;
    final private UserInPartyTrackInQueueResponse userInPartyTrackInQueueResponse;
    final private UserInPartyResponse userInPartyResponse;
    final private TrackInQueueResponse trackInQueueResponse;
    private PartyResponse partyResponse;

    private PartyServiceTest() {
        user = DataGenerator.generateUser("");
        artist = DataGenerator.generateArtist();
        party = DataGenerator.generateParty("", Set.of(user));
        previousTrack = DataGenerator.generatePreviousTrack("", party, user, Set.of(artist));
        trackInQueue = DataGenerator.generateTrackInQueue("", party, user, Set.of(artist));
        Set<Track> tracks = new HashSet<>(Set.of(trackInQueue));

        user.setParty(party);
        user.setPartyRole(PartyRole.CREATOR);
        user.addAddedTrack(trackInQueue);
        party.addTrackToPreviousTracks(previousTrack);
        party.addTrackToQueue(trackInQueue);
        artist.setTracks(tracks);

        artistResponse = DataGenerator.generateArtistResponse(artist);
        userInPartyTrackInQueueResponse = DataGenerator.generateUserInPartyTrackInQueueResponse(user);
        userInPartyResponse = DataGenerator.generateUserInPartyResponse(user);
        trackInQueueResponse = DataGenerator.generateTrackInQueueResponse(trackInQueue, Set.of(artistResponse), userInPartyTrackInQueueResponse);
        partyResponse = DataGenerator.generatePartyResponse(party, Set.of(trackInQueueResponse), Set.of(userInPartyResponse));
    }

    @Test
    void shouldRegister() {
        final PartyRequest partyRequest = DataGenerator.generatePartyRequest(party);
        when(partyRepository.save(any())).thenReturn(party);

        final Party registeredParty = partyService.register(partyRequest, user);

        assertThat(registeredParty).isSameAs(party);
        assertThat(registeredParty.getParticipants()).contains(user);
    }

    @Test
    void givenParty_whenSave_thenSuccess() {
        when(partyRepository.save(any())).thenReturn(party);

        final Party savedParty = partyService.save(party);

        assertThat(savedParty).isSameAs(party);
    }

    @Test
    void givenParty_whenDeleteByName_thenSuccess() {
        when(partyRepository.findByName(any())).thenReturn(party);
        when(partyMapper.mapPartyToPartyResponse(any())).thenReturn(partyResponse);

        doAnswer(invocation -> {
            // DB would handle this
            party.removeTrackFromQueue(trackInQueue);
            party.removePreviousTrack(previousTrack);
            user.removeAddedTrack(trackInQueue);
            return null;
        }).when(trackService).delete(any(Track.class));

        final PartyResponse response = partyService.deleteByName(user, party.getName());

        assertThat(response).isSameAs(partyResponse);
        assertThat(user.getParty()).isNull();
        assertThat(user.getPartyRole()).isNull();
        assertThat(user.getAddedTracks()).isEmpty();
        assertThat(party.getTracksInQueue()).isEmpty();
        assertThat(party.getPreviousTracks()).isEmpty();
        assertThat(artist.getTracks()).isEmpty();
    }

    @Test
    void givenParty_whenFindByName_thenSuccess() {
        when(partyRepository.findByName(any())).thenReturn(party);

        final Party foundParty = partyService.findByName(party.getName());

        assertThat(foundParty).isSameAs(party);
    }

    @Test
    void givenParty_whenFindByName_thenThrowsEntityNotFoundException() {
        doThrow(EntityNotFoundException.class).when(validator).verifyNotNull(any());

        assertThrows(EntityNotFoundException.class, () -> partyService.findByName("testParty"));
    }

    @Test
    void givenPartyWithNameAlreadyUsed_whenTryToRegister_thenThrowsNotUniqueException() {
        final PartyRequest partyRequest = DataGenerator.generatePartyRequest(party);
        final String message = "... Duplicate entry '" + partyRequest.getName() + "' for key ...";
        when(partyRepository.save(any())).thenThrow(new DataIntegrityViolationException(message));

        assertThrows(NotUniqueException.class, () -> partyService.tryToSave(party, partyRequest));
    }

    @Test
    void givenPartyWithNameAlreadyUsed_whenTryToSave_thenThrowsNotUniqueException() {
        final String message = "... Duplicate entry '" + party.getName() + "' for key ...";
        when(partyRepository.save(any())).thenThrow(new DataIntegrityViolationException(message));

        assertThrows(NotUniqueException.class, () -> partyService.tryToSave(party, null));
    }

    @Test
    void givenPartyWithNameAlreadyUsed_whenTryToSave_thenThrowsException() {
        when(partyRepository.save(any())).thenThrow(new DataIntegrityViolationException("Unknown error."));

        assertThrows(IllegalStateException.class, () -> partyService.tryToSave(party, null));
    }

    @Test
    void shouldCreateParty() {
        final PartyRequest partyRequest = DataGenerator.generatePartyRequest(party);
        when(partyRepository.save(any())).thenReturn(party);
        when(partyMapper.mapPartyToPartyResponse(any())).thenReturn(partyResponse);

        final PartyResponse response = partyService.create(user, partyRequest);

        assertThat(response).isSameAs(partyResponse);
        assertThat(user.getParty()).isSameAs(party);
        assertThat(user.getPartyRole()).isSameAs(PartyRole.CREATOR);
        assertThat(party.getParticipants()).containsOnly(user);
    }

    @Test
    void shouldLoadParty() {
        when(partyRepository.findByName(any())).thenReturn(party);
        when(partyMapper.mapPartyToPartyResponse(any())).thenReturn(partyResponse);

        final PartyResponse response = partyService.load(user, party.getName());

        assertThat(response).isSameAs(partyResponse);
    }

    @Test
    void givenPartyAndUser_whenJoinParty_thenSuccess() {
        final User user2 = DataGenerator.generateUser("2");
        final PartyRequest partyRequest = DataGenerator.generatePartyRequest(party);
        final UserInPartyResponse userInPartyResponse2 = DataGenerator.generateUserInPartyResponse(user2);
        partyResponse = DataGenerator.generatePartyResponse(party, Set.of(trackInQueueResponse), Set.of(userInPartyResponse, userInPartyResponse2));
        when(partyRepository.findByName(any())).thenReturn(party);
        when(partyMapper.mapPartyToPartyResponse(any())).thenReturn(partyResponse);

        final PartyResponse response = partyService.join(user2, partyRequest);

        assertThat(response).isSameAs(partyResponse);
        assertThat(user2.getParty()).isSameAs(party);
        assertThat(user2.getPartyRole()).isEqualTo(PartyRole.PARTICIPANT);
        assertThat(response.getParticipants()).containsOnly(userInPartyResponse, userInPartyResponse2);
    }

    @Test
    void givenParty_whenLeave_thenSuccess() {
        final User user2 = DataGenerator.generateUser("2");
        user2.setParty(party);
        user2.setPartyRole(PartyRole.PARTICIPANT);
        party.addUser(user2);
        when(partyRepository.findByName(any())).thenReturn(party);
        when(partyMapper.mapPartyToPartyResponse(any())).thenReturn(partyResponse);

        final PartyResponse response = partyService.leave(user2, party.getName());

        assertThat(response).isSameAs(partyResponse);
        assertThat(user2.getPartyRole()).isNull();
        assertThat(user2.getParty()).isNull();
        assertThat(party.getParticipants()).doesNotContain(user2);
    }

    @Test
    void givenSearchQuery_whenSearch_thenSuccess() {
        final String query = "query";
        final int offset = 0;
        final int limit = 1;
        final HashSet<PlatformType> platforms = new HashSet<>(Set.of(PlatformType.SPOTIFY));
        final HashSet<TrackSearchResultResponse> response = new HashSet<>();
        final TrackSearchResultResponse trackResponse =
                DataGenerator.generateTrackSearchResultResponse(Set.of(artist), trackInQueue);
        response.add(trackResponse);
        when(partyRepository.findByName(any())).thenReturn(party);
        when(spotifyService.search(any(User.class), any(String.class), any(Integer.class), any(Integer.class)))
                .thenReturn(response);

        final Collection<TrackSearchResultResponse> result =
                partyService.search(user, party.getName(), query, offset, limit, platforms);

        assertThat(result).containsOnly(trackResponse);
    }

    @Test
    void givenParty_whenAddTrack_thenSuccess() {
        final TrackInQueue trackInQueue2 = DataGenerator.generateTrackInQueue("2", party, user, Set.of(artist));
        final AddTrackRequest trackRequest = DataGenerator.generateAddTrackRequest(trackInQueue2);
        when(partyRepository.findByName(any())).thenReturn(party);
        when(spotifyService.fetchAndSafeTrackInfo(any(), any(), any())).thenReturn(trackInQueue2);
        when(trackMapper.mapTrackToTrackInQueueResponse(any())).thenReturn(trackInQueueResponse);

        final TrackInQueueResponse response = partyService.addTrack(user, trackRequest, party.getName());

        assertThat(response).isSameAs(trackInQueueResponse);
        assertThat(party.getTracksInQueue()).contains(trackInQueue2);
        assertThat(user.getAddedTracks()).contains(trackInQueue2);
    }

    @Test
    void givenTracksInQueue_whenGetTracks_thenSuccess() {
        when(partyRepository.findByName(any())).thenReturn(party);
        when(trackMapper.mapTrackToTrackInQueueResponse(any())).thenReturn(trackInQueueResponse);

        final Set<TrackInQueueResponse> response = partyService.getTracks(user, party.getName());

        assertThat(response).containsOnly(trackInQueueResponse);
    }

    @Test
    void givenPreviousTracks_whenGetPreviousTracks_thenSuccess() {
        final PreviousTrackResponse previousTrackResponse =
                DataGenerator.generatePreviousTrackResponse(previousTrack, Set.of(artistResponse), userInPartyTrackInQueueResponse);
        when(partyRepository.findByName(any())).thenReturn(party);
        when(trackMapper.mapPreviousTrackToPreviousTrackResponse(any())).thenReturn(previousTrackResponse);

        final Set<PreviousTrackResponse> response = partyService.getPreviousTracks(user, party.getName());

        assertThat(response).containsOnly(previousTrackResponse);
    }

    @Test
    void givenParty_whenSetSpotifyDeviceId_thenSuccess() {
        final SetSpotifyDeviceIdRequest spotifyRequest = DataGenerator.generateSpotifyRequest(party);
        final SpotifyDeviceIdResponse spotifyDeviceIdResponse = DataGenerator.generateSpotifyDeviceIdResponse(party);
        when(partyRepository.findByName(any())).thenReturn(party);
        when(partyRepository.save(any())).thenReturn(party);
        when(partyMapper.mapPartyToSpotifyDeviceId(any())).thenReturn(spotifyDeviceIdResponse);

        final SpotifyDeviceIdResponse response = partyService.setSpotifyDeviceId(user, spotifyRequest, party.getName());

        assertThat(response.getDeviceId()).isEqualTo(party.getSpotifyDeviceId());
    }

    @Test
    void givenTrackInQueue_whenRemoveTrackFromQueue_thenSuccess() {
        when(partyRepository.findByName(any())).thenReturn(party);
        when(trackService.findById(anyInt())).thenReturn(trackInQueue);
        when(trackMapper.mapTrackToTrackInQueueResponse(any())).thenReturn(trackInQueueResponse);
        doAnswer(invocation -> {
            // trackService.delete() method would handle this
            user.removeAddedTrack(trackInQueue);
            return null;
        }).when(trackService).delete(any(Track.class));

        final TrackInQueueResponse response = partyService.removeTrackFromQueue(user, party.getName(), trackInQueue.getId());

        assertThat(response).isSameAs(trackInQueueResponse);
        assertThat(party.getTracksInQueue()).isEmpty();
        assertThat(user.getAddedTracks()).isEmpty();
    }

    @Test
    void givenNowPlayingTrackAndNextTrack_whenPlayNextTrack_thenSuccess() {
        final TrackInQueue nowPlayingTrack = DataGenerator.generateTrackInQueue("now", party, user, Set.of(artist));
        nowPlayingTrack.setPlaying(true);
        final TrackInQueue nextTrack = DataGenerator.generateTrackInQueue("next", party, user, Set.of(artist));
        nextTrack.setScore(10);
        final TrackInQueueResponse nextTrackResponse =
                DataGenerator.generateTrackInQueueResponse(nowPlayingTrack, Set.of(artistResponse), userInPartyTrackInQueueResponse);
        final HashSet<TrackInQueue> tracks = new HashSet<>(Set.of(nowPlayingTrack, nextTrack));
        party.setTracksInQueue(tracks);
        party.setPreviousTracks(new HashSet<>());
        user.setAddedTracks(tracks);

        when(partyRepository.findByName(any())).thenReturn(party);
        when(trackService.getIfExistsNowPlaying(any())).thenReturn(nowPlayingTrack);
        when(trackService.getNextTrack(any())).thenReturn(nextTrack);
        when(trackMapper.mapTrackInQueueToPreviousTrack(any())).thenReturn(previousTrack);
        when(trackService.save(any())).thenReturn(nextTrack);
        when(trackMapper.mapTrackToTrackInQueueResponse(any())).thenReturn(nextTrackResponse);

        final TrackInQueueResponse response = partyService.playNextTrack(user, party.getName());

        assertThat(response).isSameAs(nextTrackResponse);
        assertThat(party.getPreviousTracks()).containsOnly(previousTrack);
        assertThat(party.getTracksInQueue()).containsOnly(nextTrack);
        assertThat(user.getAddedTracks()).containsOnly(nextTrack);
        assertThat(nextTrack.isPlaying()).isTrue();
    }
}
