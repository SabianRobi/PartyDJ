package partydj.backend.rest.controller;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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
    public PartyResponse save(final SavePartyRequest savePartyRequest) {
        partyValidator.validateOnPost(savePartyRequest);
        Party party = partyMapper.mapPartyRequestToParty(savePartyRequest);
        Party savedParty = partyService.save(party);
        return partyMapper.mapPartyToPartyResponse(savedParty);
    }

    // Delete
    @Transactional
    @DeleteMapping("/{partyName}")
    public PartyResponse delete(@PathVariable final String partyName) {
        Party party = partyService.findByName(partyName);
        partyValidator.validateOnDelete(party);
        partyService.delete(party);
        return partyMapper.mapPartyToPartyResponse(party);
    }

    // Join
    @PostMapping("/*/join")
    public PartyResponse join(final JoinPartyRequest joinRequest) {
        User user = userService.findById(1); // TODO: get authenticated user instead
        Party party = partyService.findByName(joinRequest.getName());
        partyValidator.validateOnJoin(joinRequest, party, user);

        party.addUser(user);
        user.setPartyRole(PartyRole.PARTICIPANT);
        userService.save(user);
        partyService.save(party);

        return partyMapper.mapPartyToPartyResponse(party);
    }
}
