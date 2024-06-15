package partydj.backend.rest.mapper;

import org.springframework.stereotype.Component;
import partydj.backend.rest.entity.User;
import partydj.backend.rest.entity.response.UserInPartyResponse;
import partydj.backend.rest.entity.response.UserInPartyTrackInQueueResponse;
import partydj.backend.rest.entity.response.UserResponse;

@Component
public class UserMapper {
    public UserResponse mapUserToUserResponse(final User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .isSpotifyConnected(user.getSpotifyCredential() != null)
                .partyName(user.getParty() != null ? user.getParty().getName() : null)
                .build();
    }

    public UserInPartyResponse mapUserToUserInPartyResponse(final User user) {
        return UserInPartyResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .partyRole(user.getPartyRole())
                .build();
    }

    public UserInPartyTrackInQueueResponse mapUserToUserInPartyTrackInQueueResponse(final User user) {
        return UserInPartyTrackInQueueResponse.builder()
                .username(user.getUsername())
                .build();
    }
}
