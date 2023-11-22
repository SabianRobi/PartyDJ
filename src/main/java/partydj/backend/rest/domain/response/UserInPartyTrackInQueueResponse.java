package partydj.backend.rest.domain.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserInPartyTrackInQueueResponse {
    private String username;
}
