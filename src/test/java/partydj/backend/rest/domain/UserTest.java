package partydj.backend.rest.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;
import partydj.backend.rest.domain.enums.UserType;

import java.io.File;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static partydj.backend.rest.helper.DataGenerator.*;

public class UserTest {
    private final User user;
    private final ObjectMapper objectMapper;
    private final String path;

    private UserTest() {
        user = User.builder()
                .id(1)
                .email("email")
                .username("username")
                .password("password")
                .userType(UserType.NORMAL)
                .partyRole(null)
                .spotifyCredential(null)
                .party(null)
                .addedTracks(new HashSet<>())
                .build();
        objectMapper = new ObjectMapper();
        path = "classpath:domain/user.json";
    }

    @Test
    @SneakyThrows
    void shouldSerialize() {
        final String actual = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(user);

        final File jsonFile = ResourceUtils.getFile(path);
        final String expected = Files.readString(jsonFile.toPath());

        assertThat(actual).isEqualTo(expected);

    }

    @Test
    @SneakyThrows
    void shouldDeserialize() {
        final User actual = objectMapper.readValue(
                ResourceUtils.getFile(path), User.class);

        assertThat(actual).isEqualTo(user);
    }

    @Test
    void givenUser_whenAddTrack_thenSuccess() {
        final Artist artist = generateArtist("");
        final Party party = generateParty("", Set.of(user));
        final TrackInQueue track = generateTrackInQueue("", party, user, Set.of(artist));

        user.addAddedTrack(track);

        assertThat(user.getAddedTracks()).contains(track);
    }

    @Test
    void givenUserWithAddedTrack_whenRemoveTrack_thenSuccess() {
        final Artist artist = generateArtist("");
        final Party party = generateParty("", Set.of(user));
        final TrackInQueue track = generateTrackInQueue("", party, user, Set.of(artist));
        final HashSet<TrackInQueue> tracks = new HashSet<>();
        tracks.add(track);
        user.setAddedTracks(tracks);

        user.removeAddedTrack(track);

        assertThat(user.getAddedTracks()).doesNotContain(track);
    }
}
