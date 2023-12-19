package partydj.backend.rest.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

public class SpotifyCredentialTest {
    private final SpotifyCredential spotifyCredential;
    private final ObjectMapper objectMapper;
    private final String path;

    private SpotifyCredentialTest() {
        spotifyCredential = SpotifyCredential.builder()
                .id(1)
                .state("1593bead-e671-4a0b-a195-b5165aed6410")
                .token("token")
                .refreshToken("refreshToken")
                .owner(null)
                .build();
        objectMapper = new ObjectMapper();
        path = "classpath:domain/spotifyCredential.json";
    }

    @Test
    @SneakyThrows
    void shouldSerialize() {
        final String actual = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(spotifyCredential);

        final File jsonFile = ResourceUtils.getFile(path);
        final String expected = Files.readString(jsonFile.toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    void shouldDeserialize() {
        final SpotifyCredential actual = objectMapper.readValue(
                ResourceUtils.getFile(path), SpotifyCredential.class);

        assertThat(actual).isEqualTo(spotifyCredential);
    }
}