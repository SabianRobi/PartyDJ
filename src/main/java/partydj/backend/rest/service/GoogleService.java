package partydj.backend.rest.service;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.ClientId;
import com.google.auth.oauth2.UserAuthorizer;
import com.google.auth.oauth2.UserCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import partydj.backend.rest.entity.GoogleCredential;
import partydj.backend.rest.entity.User;
import partydj.backend.rest.entity.error.ThirdPartyApiException;
import partydj.backend.rest.entity.response.PlatformCredentialResponse;
import partydj.backend.rest.entity.response.PlatformLoginUriResponse;
import partydj.backend.rest.mapper.PlatformCredentialMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

@Service
public class GoogleService {

    @Lazy
    @Autowired
    private GoogleCredentialService googleCredentialService;

    @Autowired
    private PlatformCredentialMapper platformCredentialMapper;

    @Autowired
    private Map<String, String> googleConfigs;

    private static final URI BASE_URL = URI.create("localhost:3000");

    private final UserAuthorizer userAuthorizer;

    @Autowired
    public GoogleService(final Map<String, String> googleConfigs) {
        final ArrayList<String> scopes = new ArrayList<>();
        scopes.add("https://www.googleapis.com/auth/userinfo.email");
        scopes.add("https://www.googleapis.com/auth/userinfo.profile");
        scopes.add("https://www.googleapis.com/auth/youtube.readonly");
        scopes.add("openid");

        final ClientId clientId = ClientId.of(googleConfigs.get("client-id"), googleConfigs.get("client-secret"));

        try {
            userAuthorizer = UserAuthorizer.newBuilder()
                .setClientId(clientId)
                .setCallbackUri(new URI(googleConfigs.get("redirect-uri")))
                .setScopes(scopes)
                .build();
        } catch (URISyntaxException error) {
            throw new ThirdPartyApiException("Invalid redirect uri: " + error);
        }
    }

    public PlatformLoginUriResponse generateLoginUri(final User user, final String state) {
        final URL url = userAuthorizer.getAuthorizationUrl(
                String.valueOf(user.getId()),
                state,
                BASE_URL
        );

        return new PlatformLoginUriResponse(url.toString());
    }

    public PlatformCredentialResponse processCallback(final GoogleCredential googleCredential, final String code) {

        try {
            // Get user token
            final UserAuthorizer.TokenResponseWithConfig response = userAuthorizer
                    .getTokenResponseFromAuthCodeExchange(code, userAuthorizer.getCallbackUri(), null);

            // Save tokens
            googleCredential.setToken(response.getAccessToken().getTokenValue());
            googleCredential.setRefreshToken(response.getRefreshToken());
            googleCredential.setState(null);

            googleCredentialService.save(googleCredential);

            return platformCredentialMapper.mapCredentialToCredentialResponse(googleCredential);
        } catch (IOException e) {
            throw new ThirdPartyApiException("Failed to log in with Google: " + e.getMessage());
        }
    }

    public PlatformCredentialResponse revokeTokens(final User user, final GoogleCredential googleCredential) {
        try {
            userAuthorizer.revokeAuthorization(String.valueOf(user.getId()));
        } catch (IOException e) {
            throw new ThirdPartyApiException("Failed to disconnect Google: " + e.getMessage());
        }

        return platformCredentialMapper.mapCredentialToCredentialResponse(googleCredential);
    }

    public PlatformCredentialResponse refreshToken(final GoogleCredential googleCredential) {
        UserCredentials credentials = UserCredentials
                .newBuilder()
                .setClientId(googleConfigs.get("client-id"))
                .setClientSecret(googleConfigs.get("client-secret"))
                .setRefreshToken(googleCredential.getRefreshToken())
                .build();

        try {
            final AccessToken accessToken = credentials.refreshAccessToken();
            googleCredential.setToken(accessToken.getTokenValue());
            googleCredentialService.save(googleCredential);
        } catch (IOException e) {
            throw new ThirdPartyApiException("Failed to refresh Google token: " + e.getMessage());
        }

        return platformCredentialMapper.mapCredentialToCredentialResponse(googleCredential);
    }
}
