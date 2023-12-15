package partydj.backend.rest.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import partydj.backend.rest.domain.Party;
import partydj.backend.rest.domain.PreviousTrack;
import partydj.backend.rest.domain.TrackInQueue;
import partydj.backend.rest.domain.User;
import partydj.backend.rest.domain.enums.PlatformType;
import partydj.backend.rest.domain.request.AddTrackRequest;
import partydj.backend.rest.domain.request.PartyRequest;
import partydj.backend.rest.domain.request.SetSpotifyDeviceIdRequest;
import partydj.backend.rest.domain.response.*;
import partydj.backend.rest.editor.PlatformTypeEditor;
import partydj.backend.rest.mapper.PartyMapper;
import partydj.backend.rest.mapper.TrackMapper;
import partydj.backend.rest.service.PartyService;
import partydj.backend.rest.service.UserService;
import partydj.backend.rest.validation.constraint.Name;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static partydj.backend.rest.config.PartyConfig.DEFAULT_LIMIT;

@RestController
@RequestMapping(value = "/api/v1/party", produces = "application/json")
public class PartyController {
    @Autowired
    private PartyService partyService;

    @Autowired
    private PartyMapper partyMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private TrackMapper trackMapper;

    // Create
    @PostMapping(consumes = "application/x-www-form-urlencoded")
    @ResponseStatus(HttpStatus.CREATED)
    public PartyResponse save(@Valid final PartyRequest savePartyRequest, final Authentication auth) {
        final User loggedInUser = userService.findByUsername(auth.getName());

        final Party party = partyService.create(loggedInUser, savePartyRequest);

        return partyMapper.mapPartyToPartyResponse(party);
    }

    // Get
    @GetMapping("/{partyName}")
    public PartyResponse get(@PathVariable @Name final String partyName, final Authentication auth) {
        final User loggedInUser = userService.findByUsername(auth.getName());

        final Party party = partyService.load(loggedInUser, partyName);

        return partyMapper.mapPartyToPartyResponse(party);
    }

    // Delete
    @Transactional
    @DeleteMapping("/{partyName}")
    public PartyResponse delete(@PathVariable @Name final String partyName, final Authentication auth) {
        final User loggedInUser = userService.findByUsername(auth.getName());

        final Party deletedParty = partyService.deleteByName(loggedInUser, partyName);

        return partyMapper.mapPartyToPartyResponse(deletedParty);
    }

    // Join
    @PostMapping(value = "/*/join", consumes = "application/x-www-form-urlencoded")
    public PartyResponse join(@Valid final PartyRequest joinRequest, final Authentication auth) {
        final User loggedInUser = userService.findByUsername(auth.getName());

        final Party joinedParty = partyService.join(loggedInUser, joinRequest);

        return partyMapper.mapPartyToPartyResponse(joinedParty);
    }

    // Leave
    @PostMapping("/{partyName}/leave")
    public PartyResponse leave(@PathVariable @Name final String partyName, final Authentication auth) {
        final User loggedInUser = userService.findByUsername(auth.getName());

        final Party leftParty = partyService.leave(loggedInUser, partyName);

        return partyMapper.mapPartyToPartyResponse(leftParty);
    }

    // Search
    @GetMapping("/{partyName}/search")
    public Collection<TrackSearchResultResponse> search(@RequestParam @NotNull @Min(3) final String query,
                                                        @RequestParam(defaultValue = "0") @Min(0) final int offset,
                                                        @RequestParam(defaultValue = DEFAULT_LIMIT + "") @Min(1) final int limit,
                                                        @RequestParam @NotNull @NotEmpty final Set<@NotNull PlatformType> platforms,
                                                        @PathVariable @Name final String partyName,
                                                        final Authentication auth) {
        final User loggedInUser = userService.findByUsername(auth.getName());

        return partyService.search(loggedInUser, partyName, query, offset, limit, platforms);
    }

    // Add track to queue
    @PostMapping(value = "/{partyName}/tracks", consumes = "application/x-www-form-urlencoded")
    @ResponseStatus(HttpStatus.CREATED)
    public TrackInQueueResponse addTrack(@Valid final AddTrackRequest addTrackRequest,
                                         @PathVariable @Name final String partyName,
                                         final Authentication auth) {
        final User loggedInUser = userService.findByUsername(auth.getName());

        final TrackInQueue addedTrack = partyService.addTrack(loggedInUser, addTrackRequest, partyName);

        return trackMapper.mapTrackToTrackInQueueResponse(addedTrack);
    }

    // Get tracks in queue
    @GetMapping("/{partyName}/tracks")
    public Set<TrackInQueueResponse> getTracks(@PathVariable @Name final String partyName,
                                               final Authentication auth) {
        final User loggedInUser = userService.findByUsername(auth.getName());

        final Set<TrackInQueue> tracks = partyService.getTracks(loggedInUser, partyName);

        return tracks.stream().map(track -> trackMapper.mapTrackToTrackInQueueResponse(track))
                .collect(Collectors.toSet());
    }

    // Get previous tracks
    @GetMapping("/{partyName}/tracks/previous")
    public Set<PreviousTrackResponse> getPreviousTracks(@PathVariable @Name final String partyName,
                                                        final Authentication auth) {
        final User loggedInUser = userService.findByUsername(auth.getName());

        final Set<PreviousTrack> tracks = partyService.getPreviousTracks(loggedInUser, partyName);

        return tracks.stream().map(track -> trackMapper.mapPreviousTrackToPreviousTrackResponse(track))
                .collect(Collectors.toSet());
    }

    // Set Spotify device id
    @PostMapping(value = "/{partyName}/spotifyDeviceId", consumes = "application/x-www-form-urlencoded")
    public SpotifyDeviceIdResponse setSpotifyDeviceId(@Valid final SetSpotifyDeviceIdRequest request,
                                                      @PathVariable @Name final String partyName,
                                                      final Authentication auth) {
        final User loggedInUser = userService.findByUsername(auth.getName());

        final Party updatedParty = partyService.setSpotifyDeviceId(loggedInUser, request, partyName);

        return partyMapper.mapPartyToSpotifyDeviceId(updatedParty);
    }

    // Remove track from queue
    @DeleteMapping("/{partyName}/tracks/{trackId}")
    public TrackInQueueResponse removeTrackFromQueue(@PathVariable @Name String partyName,
                                                     @PathVariable int trackId,
                                                     final Authentication auth) {
        final User loggedInUser = userService.findByUsername(auth.getName());

        final TrackInQueue deletedTrack = partyService.removeTrackFromQueue(loggedInUser, partyName, trackId);

        return trackMapper.mapTrackToTrackInQueueResponse(deletedTrack);
    }

    // Skip track, play next
    @PostMapping("/{partyName}/tracks/playNext")
    public TrackInQueueResponse playNextTrack(@PathVariable @Name String partyName,
                                              final Authentication auth) {
        final User loggedInUser = userService.findByUsername(auth.getName());

        final TrackInQueue nextTrack = partyService.playNextTrack(loggedInUser, partyName);

        return trackMapper.mapTrackToTrackInQueueResponse(nextTrack);
    }

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.registerCustomEditor(PlatformType.class, new PlatformTypeEditor());
    }
}
