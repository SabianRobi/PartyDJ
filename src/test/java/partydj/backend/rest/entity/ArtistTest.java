package partydj.backend.rest.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static partydj.backend.rest.helper.DataGenerator.*;

public class ArtistTest {
    private final Artist artist;
    private final ObjectMapper objectMapper;
    private final String path;

    private ArtistTest() {
        artist = Artist.builder()
                .id(1)
                .name("artist")
                .tracks(new HashSet<>())
                .build();
        objectMapper = new ObjectMapper();
        path = "classpath:domain/artist.json";
    }

    @Test
    @SneakyThrows
    void shouldSerialize() {
        final String actual = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(artist);

        final File jsonFile = ResourceUtils.getFile(path);
        final String expected = Files.readString(jsonFile.toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    void shouldDeserialize() {
        final Artist actual = objectMapper.readValue(
                ResourceUtils.getFile(path), Artist.class);

        assertThat(actual).isEqualTo(artist);
    }

    @Test
    void givenNewTrack_whenAddToTracks_thenSuccess() {
        final User user = generateUser("");
        final Party party = generateParty("", Set.of(user));
        final Track track = generateTrackInQueue("", party, user, Set.of(artist));

        artist.addTrack(track);

        assertThat(artist.getTracks()).contains(track);
    }

    @Test
    void givenTrack_whenRemoveFromTracks_thenSuccess() {
        final User user = generateUser("");
        final Party party = generateParty("", Set.of(user));
        final TrackInQueue track = generateTrackInQueue("", party, user, Set.of(artist));
        HashSet<Track> tracks = new HashSet<>();
        tracks.add(track);
        artist.setTracks(tracks);

        artist.removeTrack(track);

        assertThat(artist.getTracks()).isEmpty();
    }
}
