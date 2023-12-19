package partydj.backend.rest.domain.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

public class SpotifyCredentialResponseTest {
    private final SpotifyCredentialResponse spotifyCredentialResponse;
    private final ObjectMapper objectMapper;
    private final String path;

    private SpotifyCredentialResponseTest() {
        spotifyCredentialResponse = SpotifyCredentialResponse.builder()
                .token("token")
                .build();
        objectMapper = new ObjectMapper();
        path = "classpath:domain/response/spotifyCredentialResponse.json";
    }

    @Test
    @SneakyThrows
    void shouldSerialize() {
        final String actual = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(spotifyCredentialResponse);

        final File jsonFile = ResourceUtils.getFile(path);
        final String expected = Files.readString(jsonFile.toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    void shouldDeserialize() {
        final SpotifyCredentialResponse actual = objectMapper.readValue(
                ResourceUtils.getFile(path), SpotifyCredentialResponse.class);

        assertThat(actual).isEqualTo(spotifyCredentialResponse);
    }
}
