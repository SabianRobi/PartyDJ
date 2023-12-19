package partydj.backend.rest.domain.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

public class SpotifyDeviceIdResponseTest {
    private final SpotifyDeviceIdResponse spotifyDeviceIdResponse;
    private final ObjectMapper objectMapper;
    private final String path;

    private SpotifyDeviceIdResponseTest() {
        spotifyDeviceIdResponse = SpotifyDeviceIdResponse.builder()
                .deviceId("deviceId")
                .build();
        objectMapper = new ObjectMapper();
        path = "classpath:domain/response/spotifyDeviceIdResponse.json";
    }

    @Test
    @SneakyThrows
    public void shouldSerialize() {
        final String actual = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(spotifyDeviceIdResponse);

        final File jsonFile = ResourceUtils.getFile(path);
        final String expected = Files.readString(jsonFile.toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    void shouldDeserialize() {
        final SpotifyDeviceIdResponse actual = objectMapper.readValue(
                ResourceUtils.getFile(path), SpotifyDeviceIdResponse.class);

        assertThat(actual).isEqualTo(spotifyDeviceIdResponse);
    }
}
