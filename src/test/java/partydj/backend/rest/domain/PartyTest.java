package partydj.backend.rest.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;
import partydj.backend.rest.helper.DataGenerator;

import java.io.File;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static partydj.backend.rest.helper.DataGenerator.*;

public class PartyTest {
    private final Party party;
    private final ObjectMapper objectMapper;
    private final String path;

    private PartyTest() {
        party = Party.builder()
                .id(1)
                .name("party")
                .password("password")
                .spotifyDeviceId("spotifyDeviceId")
                .waitingForTrack(false)
                .tracksInQueue(new HashSet<>())
                .previousTracks(new HashSet<>())
                .participants(new HashSet<>())
                .build();
        objectMapper = new ObjectMapper();
        path = "classpath:domain/party.json";
    }

    @Test
    @SneakyThrows
    void shouldSerialize() {
        final String actual = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(party);

        final File jsonFile = ResourceUtils.getFile(path);
        final String expected = Files.readString(jsonFile.toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    void shouldDeserialize() {
        final Party actual = objectMapper.readValue(
                ResourceUtils.getFile(path), Party.class);

        assertThat(actual).isEqualTo(party);
    }

    @Test
    void givenPartyWithPassword_whenHasPassword_thenReturnsTrue() {
        assertThat(party.hasPassword()).isTrue();
    }

    @Test
    void givenPartyWithoutPassword_whenHasPassword_thenReturnsFalse() {
        final Party party = Party.builder()
                .id(1)
                .name("party")
                .spotifyDeviceId("spotifyDeviceId")
                .waitingForTrack(false)
                .tracksInQueue(new HashSet<>())
                .previousTracks(new HashSet<>())
                .participants(new HashSet<>())
                .build();

        assertThat(party.hasPassword()).isFalse();
    }

    @Test
    void givenParty_whenAddUser_thenSuccess() {
        final User user = DataGenerator.generateUser("");

        party.addUser(user);

        assertThat(party.getParticipants()).contains(user);
    }

    @Test
    void givenParty_whenRemoveUser_thenSuccess() {
        final User user = DataGenerator.generateUser("");
        final HashSet<User> participants = new HashSet<>();
        participants.add(user);
        party.setParticipants(participants);

        party.removeUser(user);

        assertThat(party.getParticipants()).doesNotContain(user);
    }

    @Test
    void givenParty_whenAddTrackToQueue_thenSuccess() {
        final User user = generateUser("");
        final Artist artist = generateArtist();
        final TrackInQueue track = generateTrackInQueue("", party, user, Set.of(artist));

        party.addTrackToQueue(track);

        assertThat(party.getTracksInQueue()).contains(track);
    }

    @Test
    void givenParty_whenAddTrackToPreviousTracks_thenSuccess() {
        final User user = generateUser("");
        final Artist artist = generateArtist();
        final PreviousTrack track = DataGenerator.generatePreviousTrack("", party, user, Set.of(artist));

        party.addTrackToPreviousTracks(track);

        assertThat(party.getPreviousTracks()).contains(track);
    }

    @Test
    void givenPartyWithTracksInQueue_whenRemoveTrackFromQueue_thenSuccess() {
        final User user = generateUser("");
        final Artist artist = generateArtist();
        final TrackInQueue track = generateTrackInQueue("", party, user, Set.of(artist));
        final HashSet<TrackInQueue> tracks = new HashSet<>();
        tracks.add(track);
        party.setTracksInQueue(tracks);

        party.removeTrackFromQueue(track);

        assertThat(party.getTracksInQueue()).doesNotContain(track);
    }
}
