package partydj.backend.rest.domain.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SetSpotifyDeviceIdRequest {
    private String deviceId;
}
