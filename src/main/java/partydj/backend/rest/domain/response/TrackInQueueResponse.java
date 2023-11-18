package partydj.backend.rest.domain.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import partydj.backend.rest.domain.enums.PlatformType;

@Getter
@Setter
@Builder
public class TrackInQueueResponse {
    private int id;
    private String uri;
    private PlatformType platformType;
    private UserInPartyTrackInQueueResponse addedBy;
}
