package partydj.backend.rest.mapper;

import org.springframework.stereotype.Component;
import partydj.backend.rest.domain.SpotifyCredential;
import partydj.backend.rest.domain.response.SpotifyCredentialResponse;

@Component
public class SpotifyCredentialMapper {
    public SpotifyCredentialResponse mapCredentialToCredentialResponse(final SpotifyCredential credential) {
        return SpotifyCredentialResponse.builder()
                .token(credential.getToken())
                .build();
    }
}
