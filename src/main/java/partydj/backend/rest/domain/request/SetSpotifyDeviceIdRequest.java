package partydj.backend.rest.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SetSpotifyDeviceIdRequest {

    @NotNull
    @NotBlank
    private String deviceId;
}
