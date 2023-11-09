package partydj.backend.rest.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import partydj.backend.rest.domain.Track;

import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartyResponse {
    private int id;
    private String name;
    private Collection<Track> inQueueTracks;
    private Collection<Track> previousTracks;
    private Collection<UserInPartyResponse> users;
}
