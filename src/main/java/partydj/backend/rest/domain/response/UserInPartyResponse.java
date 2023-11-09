package partydj.backend.rest.domain.response;

import lombok.*;
import partydj.backend.rest.domain.enums.PartyRole;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInPartyResponse {
    private int id;
    private String username;
    private PartyRole partyRole;
}
