package partydj.backend.rest.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;
import partydj.backend.rest.domain.enums.PlatformType;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

public class PreviousTrackTest {
    private final PreviousTrack previousTrack;
    private final ObjectMapper objectMapper;
    private final String path;

    private PreviousTrackTest() {
        previousTrack = PreviousTrack.builder()
                .id(1)
                .uri("uri")
                .title("title")
                .coverUri("coverUri")
                .length(1)
                .artists(new HashSet<>())
                .platformType(PlatformType.SPOTIFY)
                .addedBy(null)
                .party(null)
                .endedAt(LocalDateTime.of(2023, 12, 1, 11, 41, 25))
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        path = "classpath:domain/previousTrack.json";
    }

    @Test
    @SneakyThrows
    void shouldSerialize() {

        final String actual = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(previousTrack);

        final File jsonFile = ResourceUtils.getFile(path);
        final String expected = Files.readString(jsonFile.toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    void shouldDeserialize() {
        final PreviousTrack actual = objectMapper.readValue(
                ResourceUtils.getFile(path), PreviousTrack.class);

        assertThat(actual).isEqualTo(previousTrack);
    }
}
