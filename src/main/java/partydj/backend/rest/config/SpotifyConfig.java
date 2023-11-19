package partydj.backend.rest.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public abstract class SpotifyConfig {
    public final static String CLIENT_ID = "";
    public final static String CLIENT_SECRET = "";
    public final static String REDIRECT_URI = "http://localhost:8080/api/v1/platforms/spotify/callback";
    public final static String SCOPES = "streaming, user-modify-playback-state, user-read-currently-playing, user-read-playback-state";
}
