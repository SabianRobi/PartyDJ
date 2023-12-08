package partydj.backend.rest.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public abstract class UserConfig {
    public final static int USERNAME_MIN_LENGTH = 3;
    public final static int USERNAME_MAX_LENGTH = 32;

    public final static int PASSWORD_MIN_LENGTH = 6;
}
