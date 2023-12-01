package partydj.backend.rest.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

public class PartyTest {
    @Test
    @SneakyThrows
    public void shouldSerialize() {
        final Party party = Party.builder()
                .id(1)
                .name("party")
                .password("password")
                .spotifyDeviceId("spotifyDeviceId")
                .waitingForTrack(false)
                .tracksInQueue(new HashSet<>())
                .previousTracks(new HashSet<>())
                .participants(new HashSet<>())
                .build();
        final ObjectMapper objectMapper = new ObjectMapper();
        final String actual = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(party);

        final File jsonFile = ResourceUtils.getFile("classpath:party.json");
        final String expected = Files.readString(jsonFile.toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    void shouldDeserialize() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Party actual = objectMapper.readValue(
                ResourceUtils.getFile("classpath:party.json"), Party.class);

        final Party expected = Party.builder()
                .id(1)
                .name("party")
                .password("password")
                .spotifyDeviceId("spotifyDeviceId")
                .waitingForTrack(false)
                .tracksInQueue(new HashSet<>())
                .previousTracks(new HashSet<>())
                .participants(new HashSet<>())
                .build();

        assertThat(actual).isEqualTo(expected);
    }
}
