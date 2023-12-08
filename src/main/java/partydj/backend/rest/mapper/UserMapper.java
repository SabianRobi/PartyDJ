package partydj.backend.rest.mapper;

import org.springframework.stereotype.Component;
import partydj.backend.rest.domain.User;
import partydj.backend.rest.domain.response.UserInPartyResponse;
import partydj.backend.rest.domain.response.UserInPartyTrackInQueueResponse;
import partydj.backend.rest.domain.response.UserResponse;

@Component
public class UserMapper {
    public UserResponse mapUserToUserResponse(final User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .isSpotifyConnected(user.getSpotifyCredential() != null)
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
