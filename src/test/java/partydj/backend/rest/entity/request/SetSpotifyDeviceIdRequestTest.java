package partydj.backend.rest.entity.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

public class SetSpotifyDeviceIdRequestTest {
    private final SetSpotifyDeviceIdRequest setSpotifyDeviceIdRequest;
    private final ObjectMapper objectMapper;
    private final String path;

    private SetSpotifyDeviceIdRequestTest() {
        setSpotifyDeviceIdRequest = SetSpotifyDeviceIdRequest.builder()
                .deviceId("deviceIdTest")
                .build();
        objectMapper = new ObjectMapper();
        path = "classpath:domain/request/setSpotifyDeviceIdRequest.json";
    }

    @Test
    @SneakyThrows
    void shouldSerialize() {
        final String actual = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(setSpotifyDeviceIdRequest);

        final File jsonFile = ResourceUtils.getFile(path);
        final String expected = Files.readString(jsonFile.toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    void shouldDeserialize() {
        final SetSpotifyDeviceIdRequest actual = objectMapper.readValue(
                ResourceUtils.getFile(path), SetSpotifyDeviceIdRequest.class);

        assertThat(actual).isEqualTo(setSpotifyDeviceIdRequest);
    }
}
