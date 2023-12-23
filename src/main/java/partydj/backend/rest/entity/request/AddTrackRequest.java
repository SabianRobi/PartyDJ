package partydj.backend.rest.entity.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import partydj.backend.rest.entity.enums.PlatformType;
import partydj.backend.rest.validation.constraint.TrackUri;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddTrackRequest {

    @NotNull
    @TrackUri
    private String uri;

    @NotNull
    private PlatformType platformType;
}
