package partydj.backend.rest.entity.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

public class PlatformLoginUriResponseTest {
    private final PlatformLoginUriResponse platformLoginUriResponse;
    private final ObjectMapper objectMapper;
    private final String path;

    private PlatformLoginUriResponseTest() {
        platformLoginUriResponse = PlatformLoginUriResponse.builder()
                .uri("https://spotify.test/login")
                .build();
        objectMapper = new ObjectMapper();
        path = "classpath:domain/response/spotifyLoginUriResponseResponse.json";
    }

    @Test
    @SneakyThrows
    void shouldSerialize() {
        final String actual = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(platformLoginUriResponse);

        final File jsonFile = ResourceUtils.getFile(path);
        final String expected = Files.readString(jsonFile.toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    void shouldDeserialize() {
        final PlatformLoginUriResponse actual = objectMapper.readValue(
                ResourceUtils.getFile(path), PlatformLoginUriResponse.class);

        assertThat(actual).isEqualTo(platformLoginUriResponse);
    }
}
