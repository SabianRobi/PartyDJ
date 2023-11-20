package partydj.backend.rest.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class SpotifyConfig {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String scopes;

    @Bean
    @ConfigurationProperties(prefix = "app.platforms.spotify")
    public Map<String, String> spotifyConfigs() {
        return new HashMap<>();
    }
}
