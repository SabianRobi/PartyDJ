package partydj.backend.rest.controller;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import partydj.backend.rest.domain.Party;
import partydj.backend.rest.domain.User;
import partydj.backend.rest.domain.enums.PartyRole;
import partydj.backend.rest.domain.request.JoinPartyRequest;
import partydj.backend.rest.domain.request.SavePartyRequest;
import partydj.backend.rest.domain.response.PartyResponse;
import partydj.backend.rest.domain.response.TrackSearchResultResponse;
import partydj.backend.rest.mapper.PartyMapper;
import partydj.backend.rest.service.PartyService;
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

    // Create
    @PostMapping
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
                                                        @RequestParam(required = false) final List<String> platforms,
                                                        @RequestParam(required = false, defaultValue = "0") final int offset,
                                                        @RequestParam(required = false, defaultValue = DEFAULT_LIMIT + "") final int limit,
                                                        @PathVariable final String partyName,
                                                        final Authentication auth) {
        Party party = partyService.findByName(partyName);
        User loggedInUser = userService.findByUsername(auth.getName());

        partyValidator.validateOnSearch(party, loggedInUser, query, platforms, offset, limit);

        Collection<TrackSearchResultResponse> results = new ArrayList<>();
        if (platforms.contains("Spotify")) {
            results.addAll(spotifyController.search(query, offset, limit, loggedInUser));
        }

        return results;
    }
}
