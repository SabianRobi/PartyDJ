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

public class TrackTest {
    private final Track track;
    private final ObjectMapper objectMapper;
    private final String path;

    private TrackTest() {
        track = Track.builder()
                .id(1)
                .uri("uri")
                .title("title")
                .coverUri("coverUri")
                .length(1)
                .artists(new HashSet<>())
                .platformType(PlatformType.SPOTIFY)
                .addedBy(null)
                .party(null)
                .build();
        objectMapper = new ObjectMapper();
        path = "classpath:domain/track.json";
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
        final Track actual = objectMapper.readValue(
                ResourceUtils.getFile(path), Track.class);

        assertThat(actual).isEqualTo(track);
    }
}
