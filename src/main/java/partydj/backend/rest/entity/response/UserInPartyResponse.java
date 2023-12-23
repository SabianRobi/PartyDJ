package partydj.backend.rest.entity.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import partydj.backend.rest.entity.enums.PartyRole;
import partydj.backend.rest.validation.constraint.Name;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInPartyResponse {

    @NotNull
    private int id;

    @NotNull
    @NotBlank
    @Name
    private String username;

    @NotNull
    private PartyRole partyRole;
}
