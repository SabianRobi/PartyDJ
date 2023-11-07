package partydj.backend.rest.domain.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class SavePartyRequest {
    private String name;
    private String password;
}
