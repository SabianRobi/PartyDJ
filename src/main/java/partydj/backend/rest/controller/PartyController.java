package partydj.backend.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import partydj.backend.rest.domain.Party;
import partydj.backend.rest.domain.request.SavePartyRequest;
import partydj.backend.rest.domain.response.PartyResponse;
import partydj.backend.rest.mapper.PartyMapper;
import partydj.backend.rest.service.PartyService;
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

    @PostMapping
    public PartyResponse save(final SavePartyRequest savePartyRequest) {
        Party party = partyMapper.mapPartyRequestToParty(savePartyRequest);
        partyValidator.validateOnPost(party);
        Party savedParty = partyService.save(party);
        return partyMapper.mapPartyToPartyResponse(savedParty);
    }
}
