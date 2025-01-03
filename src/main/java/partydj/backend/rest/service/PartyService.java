package partydj.backend.rest.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import partydj.backend.rest.entity.*;
import partydj.backend.rest.entity.enums.PartyRole;
import partydj.backend.rest.entity.enums.PlatformType;
import partydj.backend.rest.entity.error.NotUniqueException;
import partydj.backend.rest.entity.request.AddTrackRequest;
import partydj.backend.rest.entity.request.PartyRequest;
import partydj.backend.rest.entity.request.SetSpotifyDeviceIdRequest;
import partydj.backend.rest.entity.response.*;
import partydj.backend.rest.mapper.PartyMapper;
import partydj.backend.rest.mapper.TrackMapper;
import partydj.backend.rest.repository.PartyRepository;
import partydj.backend.rest.validation.PartyValidator;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PartyService {

    @Autowired
    private PartyRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Lazy
    @Autowired
    private UserService userService;

    @Autowired
    private TrackService trackService;

    @Autowired
    private PartyValidator validator;

    @Autowired
    private SpotifyService spotifyService;

    @Autowired
    private YouTubeService youTubeService;

    @Autowired
    private TrackMapper trackMapper;

    @Autowired
    private ArtistService artistService;

    @Autowired
    private PartyMapper partyMapper;

    // Repository handlers

    public Party register(final PartyRequest savePartyRequest, final User loggedInUser) {
        Party party = Party.builder()
                .name(savePartyRequest.getName().trim())
                .waitingForTrack(true)
                .tracksInQueue(new HashSet<>())
                .previousTracks(new HashSet<>())
                .participants(Set.of(loggedInUser))
                .build();
        if (savePartyRequest.getPassword() != null && !savePartyRequest.getPassword().isBlank()) {
            party.setPassword(passwordEncoder.encode(savePartyRequest.getPassword().trim()));
        }

        return tryToSave(party, savePartyRequest);
    }

    public Party save(final Party party) {
        return tryToSave(party, null);
    }

    public PartyResponse deleteByName(final User loggedInUser, final String partyName) {
        final Party party = findByName(partyName);

        validator.validateOnDelete(loggedInUser, party);

        // Update users
        Set<User> users = party.getParticipants();
        users.forEach(user -> {
            user.setParty(null);
            user.setPartyRole(null);
        });
        userService.saveAll(users);

        // Removes tracks from artists & Deletes tracks
        HashSet<Track> tracks = new HashSet<>(party.getTracksInQueue());
        tracks.addAll(party.getPreviousTracks());

        tracks.forEach(track -> {
            track.getArtists().forEach(artist -> artist.removeTrack(track));
            trackService.delete(track);
        });

        repository.delete(party);

        return partyMapper.mapPartyToPartyResponse(party);
    }

    public Party findByName(final String name) {
        final Party party = repository.findByName(name);

        validator.verifyNotNull(party);

        return party;
    }

    public Party tryToSave(final Party party, final PartyRequest partyRequest) {
        try {
            return repository.save(party);
        } catch (final DataIntegrityViolationException ex) {
            if (ex.getMessage().contains("Duplicate entry")) {
                String wrongValue = ex.getMessage().split("'")[1];

                if ((partyRequest != null && Objects.equals(wrongValue, partyRequest.getName())) ||
                        (partyRequest == null && Objects.equals(wrongValue, party.getName()))) {
                    throw new NotUniqueException("name", "Already taken.");
                }
            }
            throw new IllegalStateException("Cannot save entity.");
        }
    }

    // Controller handlers

    @Transactional
    public PartyResponse create(final User loggedInUser, final PartyRequest savePartyRequest) {
        validator.validateOnCreate(loggedInUser);

        final Party party = register(savePartyRequest, loggedInUser);

        loggedInUser.setParty(party);
        loggedInUser.setPartyRole(PartyRole.CREATOR);
        userService.save(loggedInUser);

        return partyMapper.mapPartyToPartyResponse(party);
    }

    public PartyResponse load(final User loggedInUser, final String partyName) {
        validator.validateOnLoad(loggedInUser, partyName);

        final Party party = findByName(partyName);

        return partyMapper.mapPartyToPartyResponse(party);

    }

    public PartyResponse join(final User loggedInUser, final PartyRequest joinRequest) {
        final Party party = findByName(joinRequest.getName());

        validator.validateOnJoin(loggedInUser, joinRequest, party);

        party.addUser(loggedInUser);
        save(party);
        loggedInUser.setParty(party);
        loggedInUser.setPartyRole(PartyRole.PARTICIPANT);
        userService.save(loggedInUser);

        return partyMapper.mapPartyToPartyResponse(party);
    }

    public PartyResponse leave(final User loggedInUser, final String partyName) {
        final Party party = findByName(partyName);

        validator.validateOnLeave(party, loggedInUser);

        party.removeUser(loggedInUser);
        save(party);
        loggedInUser.setParty(null);
        loggedInUser.setPartyRole(null);
        userService.save(loggedInUser);

        return partyMapper.mapPartyToPartyResponse(party);
    }

    public Collection<TrackSearchResultResponse> search(final User loggedInUser, final String partyName,
                                                        final String query, final int offset, final int limit,
                                                        final Set<PlatformType> platforms) {
        final Party party = findByName(partyName);

        validator.validateOnSearch(loggedInUser, party, platforms);

        Collection<TrackSearchResultResponse> results = new ArrayList<>();

        if (platforms.contains(PlatformType.SPOTIFY)) {
            results.addAll(spotifyService.search(loggedInUser, query, offset, limit));
        }
        if (platforms.contains(PlatformType.YOUTUBE)) {
            results.addAll(youTubeService.search(loggedInUser, query, offset, limit));
        }

        return results;
    }

    public TrackInQueueResponse addTrack(final User loggedInUser, final AddTrackRequest addTrackRequest, final String partyName) {
        final Party party = findByName(partyName);

        validator.validateOnAddTrack(loggedInUser, addTrackRequest, party);

        TrackInQueue track = new TrackInQueue();
        if (addTrackRequest.getPlatformType() == PlatformType.SPOTIFY) {
             track = spotifyService.fetchAndSafeTrackInfo(loggedInUser, addTrackRequest.getUri(), party);
        } else if (addTrackRequest.getPlatformType() == PlatformType.YOUTUBE) {
            track = youTubeService.fetchAndSafeTrackInfo(loggedInUser, addTrackRequest.getUri(), party);
        }

        party.addTrackToQueue(track);
        save(party);

        loggedInUser.addAddedTrack(track);
        userService.save(loggedInUser);

        return trackMapper.mapTrackToTrackInQueueResponse(track);
    }

    public Set<TrackInQueueResponse> getTracks(final User loggedInUser, final String partyName) {
        final Party party = findByName(partyName);

        validator.validateOnGetTracks(party, loggedInUser);

        return party.getTracksInQueue().stream().map(track -> trackMapper.mapTrackToTrackInQueueResponse(track))
                .collect(Collectors.toSet());
    }

    public Set<PreviousTrackResponse> getPreviousTracks(final User loggedInUser, final String partyName) {
        final Party party = findByName(partyName);

        validator.validateOnGetTracks(party, loggedInUser);

        return party.getPreviousTracks().stream().map(track -> trackMapper.mapPreviousTrackToPreviousTrackResponse(track))
                .collect(Collectors.toSet());
    }

    public SpotifyDeviceIdResponse setSpotifyDeviceId(final User loggedInUser, final SetSpotifyDeviceIdRequest request,
                                                      final String partyName) {
        final Party party = findByName(partyName);

        validator.validateOnSetSpotifyDeviceId(loggedInUser, party);

        party.setSpotifyDeviceId(request.getDeviceId());

        return partyMapper.mapPartyToSpotifyDeviceId(save(party));
    }

    public TrackInQueueResponse removeTrackFromQueue(final User loggedInUser, final String partyName, final int trackId) {
        final Party party = findByName(partyName);
        final TrackInQueue track = trackService.findById(trackId);

        validator.validateOnRemoveTrackFromQueue(track, party, loggedInUser);

        party.removeTrackFromQueue(track);
        save(party);
        trackService.delete(track);

        return trackMapper.mapTrackToTrackInQueueResponse(track);
    }

    public TrackInQueueResponse playNextTrack(final User loggedInUser, final String partyName) {
        final Party party = findByName(partyName);
        final TrackInQueue nowPlayingTrack = trackService.getIfExistsNowPlaying(partyName); // can be null
        final TrackInQueue nextTrack = trackService.getNextTrack(partyName);

        validator.validateOnPlayNextTrack(party, loggedInUser);

        if (nextTrack.getPlatformType() == PlatformType.SPOTIFY) {
            spotifyService.playNextTrack(party, nextTrack, loggedInUser);
        }

        if (nowPlayingTrack != null) {
            PreviousTrack prevTrack = trackMapper.mapTrackInQueueToPreviousTrack(nowPlayingTrack);
            trackService.save(prevTrack);

            nowPlayingTrack.getArtists().forEach(artist -> artist.addTrack(prevTrack));
            artistService.saveAll(nowPlayingTrack.getArtists());

            party.addTrackToPreviousTracks(prevTrack);
            party.removeTrackFromQueue(nowPlayingTrack);
            save(party);

            trackService.delete(nowPlayingTrack);
        }

        nextTrack.setPlaying(true);
        return trackMapper.mapTrackToTrackInQueueResponse((TrackInQueue) trackService.save(nextTrack));
    }
}
