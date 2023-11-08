package partydj.backend.rest.domain.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UpdateUserRequest {
    private String email;
    private String username;
    private String password;
}
