package partydj.backend.rest.domain.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import partydj.backend.rest.domain.enums.PlatformType;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Setter
@Builder
public class PreviousTrackResponse {
    private String title;
    private String coverUri;
    private int length;
    private Collection<ArtistResponse> artists;
    private PlatformType platformType;
    private UserInPartyTrackInQueueResponse addedBy;
    private LocalDateTime endedAt;
}
