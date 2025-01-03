package partydj.backend.rest.entity.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import partydj.backend.rest.validation.constraint.Name;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    @NotNull
    private int id;

    @NotNull
    @Email
    private String email;

    @NotNull
    @NotBlank
    @Name
    private String username;

    @NotNull
    private boolean isSpotifyConnected;

    @NotNull
    private boolean isGoogleConnected;

    @NotNull
    private String partyName;
}