package partydj.backend.rest.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(
                                AntPathRequestMatcher.antMatcher("/css/**"),
                                AntPathRequestMatcher.antMatcher("/error/**"),
                                AntPathRequestMatcher.antMatcher("/h2-console/**"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/favicon.ico"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/v1/user"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/v1/platforms/spotify/callback/**")
                        ).permitAll()
                        .requestMatchers(
                                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/protected"),
                                new AntPathRequestMatcher("/api/v1/user/**"),
                                new AntPathRequestMatcher("/api/v1/party/**"),
                                new AntPathRequestMatcher("/api/v1/platforms/**")
                        ).hasRole("NORMAL")
                        .anyRequest().authenticated()
                )
                .formLogin(
                        (form) -> form
                                .loginPage("/login")
                                .defaultSuccessUrl("/protected")
                                .permitAll()
                )
                .logout(LogoutConfigurer::permitAll)
//                .csrf(form -> form
//                        .ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**"))
//                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .csrf(AbstractHttpConfigurer::disable)
                .headers(head -> head.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

        return http.build();
    }
}
