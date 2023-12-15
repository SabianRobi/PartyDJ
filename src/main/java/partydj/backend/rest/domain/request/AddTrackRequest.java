package partydj.backend.rest.domain.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import partydj.backend.rest.domain.enums.PlatformType;
import partydj.backend.rest.validation.constraint.TrackUri;

@Getter
@Setter
@Builder
public class AddTrackRequest {

    @NotNull
    @TrackUri
    private String uri;

    @NotNull
    private PlatformType platformType;
}
