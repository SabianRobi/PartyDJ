package partydj.backend.rest.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import partydj.backend.rest.entity.User;
import partydj.backend.rest.mapper.UserMapper;
import partydj.backend.rest.service.UserService;

import java.util.List;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000/"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(
                                AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/v1/user"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/v1/platforms/spotify/callback/**")
                        ).permitAll()
                        .requestMatchers(
                                new AntPathRequestMatcher("/api/v1/user/**"),
                                new AntPathRequestMatcher("/api/v1/party/**"),
                                new AntPathRequestMatcher("/api/v1/platforms/**")
                        ).hasRole("NORMAL")
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginProcessingUrl("/api/v1/login")
                        .successHandler((request, response, authentication) -> {
                            response.setStatus(HttpStatus.OK.value());
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");

                            User user = userService.findByUsername(authentication.getName());
                            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                            String json = ow.writeValueAsString(userMapper.mapUserToUserResponse(user));
                            response.getWriter().write(json);
                        })
                        .failureHandler((request, response, exception) ->
                                response.setStatus(HttpStatus.UNAUTHORIZED.value()))
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/api/v1/logout")
                        .logoutSuccessHandler((request, response, authentication) ->
                                response.setStatus(HttpStatus.OK.value()))
                        .permitAll()
                )
//                .csrf(form -> form.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .csrf(AbstractHttpConfigurer::disable)
//                .headers(head -> head.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) ->
                        response.setStatus(HttpStatus.UNAUTHORIZED.value())))
//                .httpBasic(Customizer.withDefaults())
                .cors(Customizer.withDefaults());
        return http.build();
    }
}
