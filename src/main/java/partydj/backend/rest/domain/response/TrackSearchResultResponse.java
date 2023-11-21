package partydj.backend.rest.domain.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import partydj.backend.rest.domain.enums.PlatformType;

import java.util.Collection;

@Getter
@Setter
@Builder
public class TrackSearchResultResponse {
    private String uri;
    private String title;
    private Collection<String> artists;
    private String coverUri;
    private int length;
    private PlatformType platformType;
}
