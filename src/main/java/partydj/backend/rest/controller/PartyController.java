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
import partydj.backend.rest.mapper.PartyMapper;
import partydj.backend.rest.service.PartyService;
import partydj.backend.rest.service.UserService;
import partydj.backend.rest.validation.PartyValidator;

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

    // Create
    @PostMapping
    public PartyResponse save(final SavePartyRequest savePartyRequest, final Authentication auth) {
        User loggedInUser = userService.findByUsername(auth.getName());
        partyValidator.validateOnPost(savePartyRequest, loggedInUser);

        Party party = partyMapper.mapPartyRequestToParty(savePartyRequest);

        loggedInUser.setPartyRole(PartyRole.CREATOR);
        userService.save(loggedInUser);
        party.addUser(loggedInUser);
        Party savedParty = partyService.save(party);
        return partyMapper.mapPartyToPartyResponse(savedParty);
    }

    // Delete
    @Transactional
    @DeleteMapping("/{partyName}")
    public PartyResponse delete(@PathVariable final String partyName, final Authentication auth) {
        User loggedInUser = userService.findByUsername(auth.getName());
        Party party = partyService.findByName(partyName);

        partyValidator.validateOnDelete(party, loggedInUser);

        partyService.delete(party);
        loggedInUser.setPartyRole(null);
        userService.save(loggedInUser);
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
}
