package partydj.backend.rest.domain.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import partydj.backend.rest.domain.enums.PlatformType;

@Getter
@Setter
@Builder
public class AddTrackRequest {
    private String uri;
    private PlatformType platformType;
}
