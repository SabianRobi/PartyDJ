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
import partydj.backend.rest.editor.PlatformTypeEditor;
import partydj.backend.rest.entity.User;
import partydj.backend.rest.entity.enums.PlatformType;
import partydj.backend.rest.entity.request.AddTrackRequest;
import partydj.backend.rest.entity.request.PartyRequest;
import partydj.backend.rest.entity.request.SetSpotifyDeviceIdRequest;
import partydj.backend.rest.entity.response.*;
import partydj.backend.rest.mapper.PartyMapper;
import partydj.backend.rest.mapper.TrackMapper;
import partydj.backend.rest.service.PartyService;
import partydj.backend.rest.service.UserService;
import partydj.backend.rest.validation.constraint.Name;

import java.util.Collection;
import java.util.Set;

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

        return partyService.create(loggedInUser, savePartyRequest);
    }

    // Get
    @GetMapping("/{partyName}")
    public PartyResponse get(@PathVariable @Name final String partyName, final Authentication auth) {
        final User loggedInUser = userService.findByUsername(auth.getName());

        return partyService.load(loggedInUser, partyName);
    }

    // Delete
    @Transactional
    @DeleteMapping("/{partyName}")
    public PartyResponse delete(@PathVariable @Name final String partyName, final Authentication auth) {
        final User loggedInUser = userService.findByUsername(auth.getName());

        return partyService.deleteByName(loggedInUser, partyName);
    }

    // Join
    @PostMapping(value = "/*/join", consumes = "application/x-www-form-urlencoded")
    public PartyResponse join(@Valid final PartyRequest joinRequest, final Authentication auth) {
        final User loggedInUser = userService.findByUsername(auth.getName());

        return partyService.join(loggedInUser, joinRequest);
    }

    // Leave
    @PostMapping("/{partyName}/leave")
    public PartyResponse leave(@PathVariable @Name final String partyName, final Authentication auth) {
        final User loggedInUser = userService.findByUsername(auth.getName());

        return partyService.leave(loggedInUser, partyName);
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

        return partyService.addTrack(loggedInUser, addTrackRequest, partyName);
    }

    // Get tracks in queue
    @GetMapping("/{partyName}/tracks")
    public Set<TrackInQueueResponse> getTracks(@PathVariable @Name final String partyName,
                                               final Authentication auth) {
        final User loggedInUser = userService.findByUsername(auth.getName());

        return partyService.getTracks(loggedInUser, partyName);
    }

    // Get previous tracks
    @GetMapping("/{partyName}/tracks/previous")
    public Set<PreviousTrackResponse> getPreviousTracks(@PathVariable @Name final String partyName,
                                                        final Authentication auth) {
        final User loggedInUser = userService.findByUsername(auth.getName());

        return partyService.getPreviousTracks(loggedInUser, partyName);
    }

    // Set Spotify device id
    @PostMapping(value = "/{partyName}/spotifyDeviceId", consumes = "application/x-www-form-urlencoded")
    public SpotifyDeviceIdResponse setSpotifyDeviceId(@Valid final SetSpotifyDeviceIdRequest request,
                                                      @PathVariable @Name final String partyName,
                                                      final Authentication auth) {
        final User loggedInUser = userService.findByUsername(auth.getName());

        return partyService.setSpotifyDeviceId(loggedInUser, request, partyName);
    }

    // Remove track from queue
    @DeleteMapping("/{partyName}/tracks/{trackId}")
    public TrackInQueueResponse removeTrackFromQueue(@PathVariable @Name String partyName,
                                                     @PathVariable int trackId,
                                                     final Authentication auth) {
        final User loggedInUser = userService.findByUsername(auth.getName());

        return partyService.removeTrackFromQueue(loggedInUser, partyName, trackId);
    }

    // Skip track, play next
    @PostMapping("/{partyName}/tracks/playNext")
    public TrackInQueueResponse playNextTrack(@PathVariable @Name String partyName,
                                              final Authentication auth) {
        final User loggedInUser = userService.findByUsername(auth.getName());

        return partyService.playNextTrack(loggedInUser, partyName);
    }

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.registerCustomEditor(PlatformType.class, new PlatformTypeEditor());
    }
}
