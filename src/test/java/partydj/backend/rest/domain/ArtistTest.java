package partydj.backend.rest.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

public class ArtistTest {
    @Test
    @SneakyThrows
    public void shouldSerialize() {
        final Artist artist = Artist.builder()
                .id(1)
//                .name("artist")
                .tracks(new HashSet<>())
                .build();
        final ObjectMapper objectMapper = new ObjectMapper();
        final String actual = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(artist);

        final File jsonFile = ResourceUtils.getFile("classpath:artist.json");
        final String expected = Files.readString(jsonFile.toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    void shouldDeserialize() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Artist actual = objectMapper.readValue(
                ResourceUtils.getFile("classpath:artist.json"), Artist.class);

        final Artist expected = Artist.builder()
                .id(1)
                .name("artist")
                .tracks(new HashSet<>())
                .build();

        assertThat(actual).isEqualTo(expected);
    }
}
