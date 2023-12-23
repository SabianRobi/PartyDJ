package partydj.backend.rest.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;
import partydj.backend.rest.entity.enums.PlatformType;

import java.io.File;
import java.nio.file.Files;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

public class TrackInQueueTest {
    private final TrackInQueue track;
    private final ObjectMapper objectMapper;
    private final String path;

    private TrackInQueueTest() {
        track = TrackInQueue.builder()
                .id(1)
                .uri("uri")
                .title("title")
                .coverUri("coverUri")
                .length(1)
                .artists(new HashSet<>())
                .platformType(PlatformType.SPOTIFY)
                .addedBy(null)
                .party(null)
                .score(0)
                .isPlaying(false)
                .build();
        objectMapper = new ObjectMapper();
        path = "classpath:domain/trackInQueue.json";
    }

    @Test
    @SneakyThrows
    void shouldSerialize() {
        final String actual = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(track);

        final File jsonFile = ResourceUtils.getFile(path);
        final String expected = Files.readString(jsonFile.toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    void shouldDeserialize() {
        final TrackInQueue actual = objectMapper.readValue(
                ResourceUtils.getFile(path), TrackInQueue.class);

        assertThat(actual).isEqualTo(track);
    }
}
