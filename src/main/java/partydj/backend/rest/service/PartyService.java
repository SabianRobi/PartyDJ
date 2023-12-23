package partydj.backend.rest.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import partydj.backend.rest.domain.*;
import partydj.backend.rest.domain.enums.PartyRole;
import partydj.backend.rest.domain.enums.PlatformType;
import partydj.backend.rest.domain.error.NotUniqueException;
import partydj.backend.rest.domain.request.AddTrackRequest;
import partydj.backend.rest.domain.request.PartyRequest;
import partydj.backend.rest.domain.request.SetSpotifyDeviceIdRequest;
import partydj.backend.rest.domain.response.TrackSearchResultResponse;
import partydj.backend.rest.mapper.TrackMapper;
import partydj.backend.rest.repository.PartyRepository;
import partydj.backend.rest.validation.PartyValidator;

import java.util.*;

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
    private TrackMapper trackMapper;

    @Autowired
    private ArtistService artistService;

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

    public Party deleteByName(final User loggedInUser, final String partyName) {
        final Party party = findByName(partyName);

        validator.validateOnDelete(loggedInUser, party);

        // Update users
        Set<User> users = party.getParticipants();
        users.forEach(user -> {
            user.setParty(null);
            user.setPartyRole(null);
        });
        userService.saveAll(users);

        // Deletes tracks
        HashSet<Track> tracks = new HashSet<>(party.getTracksInQueue());
        tracks.addAll(party.getPreviousTracks());

        tracks.forEach(track -> trackService.delete(track));

        repository.delete(party);

        return party;
    }

    public Party findByName(final String name) {
        final Party party = repository.findByName(name);

        if (party == null) {
            throw new EntityNotFoundException("Party does not exists.");
        }

        return party;
    }

    public Party tryToSave(final Party party, final PartyRequest partyRequest) {
        try {
            return repository.save(party);
        } catch (final DataIntegrityViolationException ex) {
            if (ex.getMessage().contains("Duplicate entry")) {
                String wrongValue = ex.getMessage().split("'")[1];

                if (partyRequest != null) {
                    if (Objects.equals(wrongValue, partyRequest.getName())) {
                        throw new NotUniqueException("name", "Already taken.");
                    }
                } else {
                    if (Objects.equals(wrongValue, party.getName())) {
                        throw new NotUniqueException("name", "Already taken.");
                    }
                }
            }
            throw new IllegalStateException("Cannot save entity.");
        }
    }


    // Controller handlers

    @Transactional
    public Party create(final User loggedInUser, final PartyRequest savePartyRequest) {
        validator.validateOnCreate(loggedInUser);

        final Party party = register(savePartyRequest, loggedInUser);

        loggedInUser.setParty(party);
        loggedInUser.setPartyRole(PartyRole.CREATOR);
        userService.save(loggedInUser);

        return party;
    }

    public Party load(final User loggedInUser, final String partyName) {
        if (loggedInUser.getParty() == null) {
            throw new IllegalStateException("You are not in a party.");
        }
        if (!Objects.equals(loggedInUser.getParty().getName(), partyName)) {
            throw new IllegalStateException("You are not in the given party.");
        }

        return findByName(partyName);
    }

    public Party join(final User loggedInUser, final PartyRequest joinRequest) {
        final Party party = findByName(joinRequest.getName());

        validator.validateOnJoin(loggedInUser, joinRequest, party);

        party.addUser(loggedInUser);
        save(party);
        loggedInUser.setParty(party);
        loggedInUser.setPartyRole(PartyRole.PARTICIPANT);
        userService.save(loggedInUser);

        return party;
    }

    public Party leave(final User loggedInUser, final String partyName) {
        final Party party = findByName(partyName);

        validator.validateOnLeave(party, loggedInUser);

        party.removeUser(loggedInUser);
        save(party);
        loggedInUser.setParty(null);
        loggedInUser.setPartyRole(null);
        userService.save(loggedInUser);

        return party;
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

        return results;
    }

    public TrackInQueue addTrack(final User loggedInUser, final AddTrackRequest addTrackRequest, final String partyName) {
        final Party party = findByName(partyName);

        validator.validateOnAddTrack(loggedInUser, addTrackRequest, party);

        final TrackInQueue track = spotifyService.fetchAndSafeTrackInfo(loggedInUser, addTrackRequest.getUri(), party);
        party.addTrackToQueue(track);
        save(party);

        loggedInUser.addAddedTrack(track);
        userService.save(loggedInUser);

        return track;
    }

    public Set<TrackInQueue> getTracks(final User loggedInUser, final String partyName) {
        final Party party = findByName(partyName);

        validator.validateOnGetTracks(party, loggedInUser);

        return party.getTracksInQueue();
    }

    public Set<PreviousTrack> getPreviousTracks(final User loggedInUser, final String partyName) {
        final Party party = findByName(partyName);

        validator.validateOnGetTracks(party, loggedInUser);

        return party.getPreviousTracks();
    }

    public Party setSpotifyDeviceId(final User loggedInUser, final SetSpotifyDeviceIdRequest request,
                                    final String partyName) {
        final Party party = findByName(partyName);

        validator.validateOnSetSpotifyDeviceId(loggedInUser, party);

        party.setSpotifyDeviceId(request.getDeviceId());
        return save(party);
    }

    public TrackInQueue removeTrackFromQueue(final User loggedInUser, final String partyName, final int trackId) {
        final Party party = findByName(partyName);
        final TrackInQueue track = trackService.findById(trackId);

        validator.validateOnRemoveTrackFromQueue(track, party, loggedInUser);

        party.removeTrackFromQueue(track);
        save(party);
        trackService.delete(track);

        return track;
    }

    public TrackInQueue playNextTrack(final User loggedInUser, final String partyName) {
        final Party party = findByName(partyName);
        final TrackInQueue nowPlayingTrack = trackService.getNowPlaying(partyName); // can be null
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
        return (TrackInQueue) trackService.save(nextTrack);
    }
}
