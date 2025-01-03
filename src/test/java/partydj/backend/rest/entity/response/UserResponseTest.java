package partydj.backend.rest.entity.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

public class UserResponseTest {
    private final UserResponse userResponse;
    private final ObjectMapper objectMapper;
    private final String path;

    private UserResponseTest() {
        userResponse = UserResponse.builder()
                .id(1)
                .email("e@ma.il")
                .username("username")
                .isSpotifyConnected(false)
                .isGoogleConnected(false)
                .partyName(null)
                .build();
        objectMapper = new ObjectMapper();
        path = "classpath:domain/response/userResponse.json";
    }

    @Test
    @SneakyThrows
    void shouldSerialize() {
        final String actual = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(userResponse);

        final File jsonFile = ResourceUtils.getFile(path);
        final String expected = Files.readString(jsonFile.toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    void shouldDeserialize() {
        final UserResponse actual = objectMapper.readValue(
                ResourceUtils.getFile(path), UserResponse.class);

        assertThat(actual).isEqualTo(userResponse);
    }
}
