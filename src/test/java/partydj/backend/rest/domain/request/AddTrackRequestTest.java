package partydj.backend.rest.domain.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;
import partydj.backend.rest.domain.enums.PlatformType;

import java.io.File;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

public class AddTrackRequestTest {
    private final AddTrackRequest addTrackRequest;
    private final ObjectMapper objectMapper;
    private final String path;

    private AddTrackRequestTest() {
        addTrackRequest = AddTrackRequest.builder()
                .uri("spotify:track:something")
                .platformType(PlatformType.SPOTIFY)
                .build();
        objectMapper = new ObjectMapper();
        path = "classpath:domain/request/addTrackRequest.json";
    }

    @Test
    @SneakyThrows
    public void shouldSerialize() {
        final String actual = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(addTrackRequest);

        final File jsonFile = ResourceUtils.getFile(path);
        final String expected = Files.readString(jsonFile.toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    void shouldDeserialize() {
        final AddTrackRequest actual = objectMapper.readValue(
                ResourceUtils.getFile(path), AddTrackRequest.class);

        assertThat(actual).isEqualTo(addTrackRequest);
    }
}
