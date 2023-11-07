package partydj.backend.rest.mapper;

import org.springframework.stereotype.Component;
import partydj.backend.rest.domain.Party;
import partydj.backend.rest.domain.request.SavePartyRequest;
import partydj.backend.rest.domain.response.PartyResponse;

import java.util.ArrayList;

@Component
public class PartyMapper {
    public Party mapPartyRequestToParty(final SavePartyRequest partyRequest) {
        Party party = Party.builder()
                .name(partyRequest.getName().trim())
                .waitingForTrack(true)
                .inQueueTracks(new ArrayList<>())
                .previousTracks(new ArrayList<>())
                .users(new ArrayList<>())
                .build();
        if(partyRequest.getPassword() != null && !partyRequest.getPassword().trim().isEmpty()) {
            party.setPassword(partyRequest.getPassword().trim());
        }
        return party;
    }

    public PartyResponse mapPartyToPartyResponse(Party party) {
        return PartyResponse.builder()
                .id(party.getId())
                .name(party.getName())
                .inQueueTracks(party.getInQueueTracks())
                .previousTracks(party.getPreviousTracks())
                .users(party.getUsers())
                .build();
    }
}
