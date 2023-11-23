package partydj.backend.rest.controller;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import partydj.backend.rest.domain.Party;
import partydj.backend.rest.domain.PreviousTrack;
import partydj.backend.rest.domain.TrackInQueue;
import partydj.backend.rest.domain.User;
import partydj.backend.rest.domain.enums.PartyRole;
import partydj.backend.rest.domain.enums.PlatformType;
import partydj.backend.rest.domain.request.AddTrackRequest;
import partydj.backend.rest.domain.request.JoinPartyRequest;
import partydj.backend.rest.domain.request.SavePartyRequest;
import partydj.backend.rest.domain.request.SetSpotifyDeviceIdRequest;
import partydj.backend.rest.domain.response.PartyResponse;
import partydj.backend.rest.domain.response.PreviousTrackResponse;
import partydj.backend.rest.domain.response.TrackInQueueResponse;
import partydj.backend.rest.domain.response.TrackSearchResultResponse;
import partydj.backend.rest.editor.PlatformTypeEditor;
import partydj.backend.rest.mapper.PartyMapper;
import partydj.backend.rest.mapper.TrackMapper;
import partydj.backend.rest.service.PartyService;
import partydj.backend.rest.service.PreviousTrackService;
import partydj.backend.rest.service.TrackService;
import partydj.backend.rest.service.UserService;
import partydj.backend.rest.validation.PartyValidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static partydj.backend.rest.config.PartyConfig.DEFAULT_LIMIT;

@RestController
@RequestMapping("/api/v1/party")
public class PartyController {
    @Autowired
    private PartyService partyService;

    @Autowired
    private PartyValidator partyValidator;

    @Autowired
    private PartyMapper partyMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private SpotifyController spotifyController;

    @Autowired
    private TrackMapper trackMapper;

    @Autowired
    private TrackService trackService;

    @Autowired
    private PreviousTrackService previousTrackService;

    // Create
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PartyResponse save(final SavePartyRequest savePartyRequest, final Authentication auth) {
        User loggedInUser = userService.findByUsername(auth.getName());
        partyValidator.validateOnPost(savePartyRequest, loggedInUser);

        Party party = partyMapper.mapPartyRequestToParty(savePartyRequest);

        loggedInUser.setPartyRole(PartyRole.CREATOR);
        userService.save(loggedInUser);
        party.addUser(loggedInUser);
        Party savedParty = partyService.register(party);
        return partyMapper.mapPartyToPartyResponse(savedParty);
    }

    // Get
    @GetMapping("/{partyName}")
    public PartyResponse get(@PathVariable final String partyName, final Authentication auth) {
        User loggedInUser = userService.findByUsername(auth.getName());
        Party party = partyService.findByName(partyName);

        partyValidator.validateOnGet(party, loggedInUser);

        return partyMapper.mapPartyToPartyResponse(party);
    }

    // Delete
    @Transactional
    @DeleteMapping("/{partyName}")
    public PartyResponse delete(@PathVariable final String partyName, final Authentication auth) {
        User loggedInUser = userService.findByUsername(auth.getName());
        Party party = partyService.findByName(partyName);

        partyValidator.validateOnDelete(party, loggedInUser);

        for (User participant : party.getParticipants()) {
            participant.setPartyRole(null);
            userService.save(participant);
        }
        partyService.delete(party);

        return partyMapper.mapPartyToPartyResponse(party);
    }

    // Join
    @PostMapping("/*/join")
    public PartyResponse join(final JoinPartyRequest joinRequest, final Authentication auth) {
        User loggedInUser = userService.findByUsername(auth.getName());

        partyValidator.validateOnJoin(joinRequest, loggedInUser);

        Party party = partyService.findByName(joinRequest.getName());
        party.addUser(loggedInUser);
        loggedInUser.setPartyRole(PartyRole.PARTICIPANT);
        userService.save(loggedInUser);
        partyService.save(party);

        return partyMapper.mapPartyToPartyResponse(party);
    }

    // Leave
    @PostMapping("/{partyName}/leave")
    public PartyResponse leave(@PathVariable final String partyName, final Authentication auth) {
        Party party = partyService.findByName(partyName);
        User loggedInUser = userService.findByUsername(auth.getName());

        partyValidator.validateOnLeave(party, loggedInUser);

        party.removeUser(loggedInUser);
        loggedInUser.setPartyRole(null);
        userService.save(loggedInUser);
        partyService.save(party);
        return partyMapper.mapPartyToPartyResponse(party);
    }

    // Search
    @GetMapping("/{partyName}/search")
    public Collection<TrackSearchResultResponse> search(@RequestParam(required = false) final String query,
                                                        @RequestParam(required = false) final List<PlatformType> platforms,
                                                        @RequestParam(required = false, defaultValue = "0") final int offset,
                                                        @RequestParam(required = false, defaultValue = DEFAULT_LIMIT + "") final int limit,
                                                        @PathVariable final String partyName,
                                                        final Authentication auth) {
        Party party = partyService.findByName(partyName);
        User loggedInUser = userService.findByUsername(auth.getName());

        partyValidator.validateOnSearch(party, loggedInUser, query, platforms, offset, limit);

        Collection<TrackSearchResultResponse> results = new ArrayList<>();
        if (platforms.contains(PlatformType.SPOTIFY)) {
            results.addAll(spotifyController.search(query, offset, limit, loggedInUser));
        }

        return results;
    }

    // Add track to queue
    @PostMapping("/{partyName}/tracks")
    @ResponseStatus(HttpStatus.CREATED)
    public TrackInQueueResponse addTrack(final AddTrackRequest addTrackRequest,
                                         @PathVariable final String partyName,
                                         final Authentication auth) {
        Party party = partyService.findByName(partyName);
        User loggedInUser = userService.findByUsername(auth.getName());

        partyValidator.validateOnAddTrack(addTrackRequest, party, loggedInUser);

        TrackInQueue track = spotifyController.fetchTrackInfo(addTrackRequest.getUri(), loggedInUser, party);
        trackService.save(track);
        party.addTrackToQueue(track);
        partyService.save(party);

        return trackMapper.mapTrackToTrackInQueueResponse(track);
    }

    // Get tracks in queue
    @GetMapping("/{partyName}/tracks")
    public Collection<TrackInQueueResponse> getTracks(@PathVariable final String partyName,
                                                      final Authentication auth) {
        Party party = partyService.findByName(partyName);
        User loggedInUser = userService.findByUsername(auth.getName());

        partyValidator.validateOnGetTracks(party, loggedInUser);

        Collection<TrackInQueue> tracks = party.getTracksInQueue();
        return tracks.stream().map(track -> trackMapper.mapTrackToTrackInQueueResponse(track)).toList();
    }

    // Get previous tracks
    @GetMapping("/{partyName}/tracks/previous")
    public Collection<PreviousTrackResponse> getPreviousTracks(@PathVariable final String partyName,
                                                               final Authentication auth) {
        Party party = partyService.findByName(partyName);
        User loggedInUser = userService.findByUsername(auth.getName());

        partyValidator.validateOnGetPreviousTracks(party, loggedInUser);

        Collection<PreviousTrack> tracks = party.getPreviousTracks();
        return tracks.stream().map(track -> trackMapper.mapPreviousTrackToPreviousTrackResponse(track)).toList();
    }

    // Set Spotify device id
    @PostMapping("/{partyName}/spotifyDeviceId")
    public String setSpotifyDeviceId(final SetSpotifyDeviceIdRequest request,
                                     @PathVariable final String partyName,
                                     final Authentication auth) {
        Party party = partyService.findByName(partyName);
        User loggedInUser = userService.findByUsername(auth.getName());

        partyValidator.validateOnSetSpotifyDeviceId(request, party, loggedInUser);

        party.setSpotifyDeviceId(request.getDeviceId());
        partyService.save(party);
        return request.getDeviceId();
    }

    // Remove track from queue
    @DeleteMapping("/{partyName}/tracks/{trackId}")
    public TrackInQueueResponse removeTrackFromQueue(@PathVariable String partyName,
                                                     @PathVariable int trackId,
                                                     Authentication auth) {
        Party party = partyService.findByName(partyName);
        User loggedInUser = userService.findByUsername(auth.getName());
        TrackInQueue track = trackService.findById(trackId);

        partyValidator.validateOnRemoveTrackFromQueue(track, party, loggedInUser);

        party.removeTrackFromQueue(track);
        partyService.save(party);
        trackService.delete(track);
        return trackMapper.mapTrackToTrackInQueueResponse(track);
    }

    // Skip track, play next
    @PostMapping("/{partyName}/tracks/playNext")
    public TrackInQueueResponse playNextTrack(@PathVariable String partyName,
                                              Authentication auth) {
        Party party = partyService.findByName(partyName);
        User loggedInUser = userService.findByUsername(auth.getName());
        TrackInQueue nowPlayingTrack = trackService.getNowPlaying(partyName);
        TrackInQueue nextTrack = trackService.getNextTrack(partyName);

        partyValidator.validateOnPlayNextTrack(party, loggedInUser, nextTrack);


        if (nextTrack.getPlatformType() == PlatformType.SPOTIFY) {
            spotifyController.playNextTrack(party, nextTrack, loggedInUser);
        }

        if (nowPlayingTrack != null) {
            PreviousTrack prevTrack = trackMapper.mapTrackInQueueToPreviousTrack(nowPlayingTrack);
            previousTrackService.save(prevTrack);
            party.addTrackToPreviousTracks(prevTrack);
            party.removeTrackFromQueue(nowPlayingTrack);
            partyService.save(party);

            trackService.delete(nowPlayingTrack);
        }

        nextTrack.setPlaying(true);
        trackService.save(nextTrack);

        return trackMapper.mapTrackToTrackInQueueResponse(nextTrack);
    }

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.registerCustomEditor(PlatformType.class, new PlatformTypeEditor());
    }
}
