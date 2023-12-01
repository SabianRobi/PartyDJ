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
    @Test
    @SneakyThrows
    public void shouldSerialize() {
        final PreviousTrack track = PreviousTrack.builder()
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
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        final String actual = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(track);

        final File jsonFile = ResourceUtils.getFile("classpath:previousTrack.json");
        final String expected = Files.readString(jsonFile.toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    void shouldDeserialize() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        final PreviousTrack actual = objectMapper.readValue(
                ResourceUtils.getFile("classpath:previousTrack.json"), PreviousTrack.class);

        final PreviousTrack expected = PreviousTrack.builder()
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

        assertThat(actual).isEqualTo(expected);
    }
}
