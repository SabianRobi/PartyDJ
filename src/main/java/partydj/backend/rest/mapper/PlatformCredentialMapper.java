package partydj.backend.rest.mapper;

import org.springframework.stereotype.Component;
import partydj.backend.rest.entity.PlatformCredential;
import partydj.backend.rest.entity.response.PlatformCredentialResponse;

@Component
public class PlatformCredentialMapper {
    public PlatformCredentialResponse mapCredentialToCredentialResponse(final PlatformCredential platformCredential) {
        return PlatformCredentialResponse.builder()
                .token(platformCredential.getToken())
                .build();
    }
}
