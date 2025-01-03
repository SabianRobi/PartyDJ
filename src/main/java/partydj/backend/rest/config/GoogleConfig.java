package partydj.backend.rest.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class GoogleConfig {
    private String clientId;
    private String clientSecret;
    private String redirectUri;

    @Bean
    @ConfigurationProperties(prefix = "app.platforms.google")
    public Map<String, String> googleConfigs() {
        return new HashMap<>();
    }
}
