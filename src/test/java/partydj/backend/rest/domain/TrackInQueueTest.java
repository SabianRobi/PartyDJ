package partydj.backend.rest.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;
import partydj.backend.rest.domain.enums.PlatformType;

import java.io.File;
import java.nio.file.Files;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

public class TrackInQueueTest {
    @Test
    @SneakyThrows
    public void shouldSerialize() {
        final TrackInQueue track = TrackInQueue.builder()
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
        final ObjectMapper objectMapper = new ObjectMapper();
        final String actual = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(track);

        final File jsonFile = ResourceUtils.getFile("classpath:domain/trackInQueue.json");
        final String expected = Files.readString(jsonFile.toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    void shouldDeserialize() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final TrackInQueue actual = objectMapper.readValue(
                ResourceUtils.getFile("classpath:domain/trackInQueue.json"), TrackInQueue.class);

        final TrackInQueue expected = TrackInQueue.builder()
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

        assertThat(actual).isEqualTo(expected);
    }
}
