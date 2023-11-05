package partydj.backend.rest.transformer;

import org.springframework.stereotype.Component;
import partydj.backend.rest.domain.User;
import partydj.backend.rest.domain.response.UserResponse;

@Component
public class UserTransformer {
    public UserResponse transformUserToUserResponse(final User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .build();
    }
}
