package partydj.backend.rest.entity.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

public class PlatformPlatformCredentialResponseTest {
    private final PlatformCredentialResponse platformCredentialResponse;
    private final ObjectMapper objectMapper;
    private final String path;

    private PlatformPlatformCredentialResponseTest() {
        platformCredentialResponse = PlatformCredentialResponse.builder()
                .token("token")
                .build();
        objectMapper = new ObjectMapper();
        path = "classpath:domain/response/spotifyCredentialResponse.json";
    }

    @Test
    @SneakyThrows
    void shouldSerialize() {
        final String actual = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(platformCredentialResponse);

        final File jsonFile = ResourceUtils.getFile(path);
        final String expected = Files.readString(jsonFile.toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    void shouldDeserialize() {
        final PlatformCredentialResponse actual = objectMapper.readValue(
                ResourceUtils.getFile(path), PlatformCredentialResponse.class);

        assertThat(actual).isEqualTo(platformCredentialResponse);
    }
}
