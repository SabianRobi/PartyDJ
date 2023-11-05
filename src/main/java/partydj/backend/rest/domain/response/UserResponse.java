package partydj.backend.rest.domain.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserResponse {
    private int id;
    private String email;
    private String username;
//    private UserType userType;
//    private PartyRole partyRole;
//    private int spotifyCredentialId;
//    private int partyId;
// TODO: isSpotifyConnected should be added

    @Builder
    public UserResponse(int id, String email, String username) {
        this.id = id;
        this.email = email;
        this.username = username;
    }
}