package partydj.backend.rest.entity.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import partydj.backend.rest.validation.constraint.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetSpotifyTokensRequest {
    @NotNull
    @NotBlank
    private String code;

    @NotNull
    @UUID
    private java.util.UUID state;
}
