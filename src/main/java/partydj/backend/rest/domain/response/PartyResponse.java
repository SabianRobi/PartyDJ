package partydj.backend.rest.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartyResponse {
    private int id;
    private String name;
    private Collection<TrackInQueueResponse> tracksInQueue;
//    private Collection<TrackInQueueResponse> previousTracks;
    private Collection<UserInPartyResponse> participants;
}
