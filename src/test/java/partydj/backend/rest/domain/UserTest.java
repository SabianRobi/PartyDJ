package partydj.backend.rest.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;
import partydj.backend.rest.domain.enums.UserType;

import java.io.File;
import java.nio.file.Files;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {
    @Test
    @SneakyThrows
    public void shouldSerialize() {
        final User user = User.builder()
                .id(1)
                .email("email")
                .username("username")
                .password("password")
                .userType(UserType.NORMAL)
                .partyRole(null)
                .spotifyCredential(null)
                .party(null)
                .addedTracks(new HashSet<>())
                .build();
        final ObjectMapper objectMapper = new ObjectMapper();
        final String actual = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(user);

        final File jsonFile = ResourceUtils.getFile("classpath:domain/user.json");
        final String expected = Files.readString(jsonFile.toPath());

        assertThat(actual).isEqualTo(expected);

    }

    @Test
    @SneakyThrows
    void shouldDeserialize() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final User actual = objectMapper.readValue(
                ResourceUtils.getFile("classpath:domain/user.json"), User.class);

        final User expected = User.builder()
                .id(1)
                .email("email")
                .username("username")
                .password("password")
                .userType(UserType.NORMAL)
                .partyRole(null)
                .spotifyCredential(null)
                .party(null)
                .addedTracks(new HashSet<>())
                .build();

        assertThat(actual).isEqualTo(expected);
    }
}
