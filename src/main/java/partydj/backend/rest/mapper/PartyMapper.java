package partydj.backend.rest.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import partydj.backend.rest.domain.Party;
import partydj.backend.rest.domain.request.SavePartyRequest;
import partydj.backend.rest.domain.response.PartyResponse;
import partydj.backend.rest.domain.response.SpotifyDeviceIdResponse;

import java.util.HashSet;

@Component
public class PartyMapper {

    @Autowired
    UserMapper userMapper;

    @Autowired
    TrackMapper trackMapper;

    public Party mapPartyRequestToParty(final SavePartyRequest partyRequest) {
        Party party = Party.builder()
                .name(partyRequest.getName())
                .waitingForTrack(true)
                .tracksInQueue(new HashSet<>())
                .previousTracks(new HashSet<>())
                .participants(new HashSet<>())
                .build();
        if (partyRequest.getPassword() != null && !partyRequest.getPassword().isBlank()) {
            party.setPassword(partyRequest.getPassword().trim());
        }
        return party;
    }

    public PartyResponse mapPartyToPartyResponse(final Party party) {
        return PartyResponse.builder()
                .id(party.getId())
                .name(party.getName())
                .tracksInQueue(party.getTracksInQueue().stream().map(track -> trackMapper.mapTrackToTrackInQueueResponse(track)).toList())
//                .previousTracks(party.getPreviousTracks().stream().map(track -> trackMapper.mapTrackToTrackInQueue(track)).toList())
                .participants(party.getParticipants().stream().map(user -> userMapper.mapUserToUserInPartyResponse(user)).toList())
                .build();
    }

    public SpotifyDeviceIdResponse mapPartyToSpotifyDeviceId(final Party party) {
        return new SpotifyDeviceIdResponse(party.getSpotifyDeviceId());
    }
}
