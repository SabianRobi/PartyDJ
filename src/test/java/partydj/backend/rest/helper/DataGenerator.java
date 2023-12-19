package partydj.backend.rest.helper;

import org.springframework.stereotype.Component;
import partydj.backend.rest.domain.*;
import partydj.backend.rest.domain.enums.PartyRole;
import partydj.backend.rest.domain.enums.PlatformType;
import partydj.backend.rest.domain.enums.UserType;
import partydj.backend.rest.domain.response.ArtistResponse;
import partydj.backend.rest.domain.response.UserInPartyResponse;
import partydj.backend.rest.domain.response.UserInPartyTrackInQueueResponse;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataGenerator {

    public static User generateUser(final String serial) {
        return User.builder()
                .email(serial + "_us@e.r")
                .username(serial + "user")
                .password(serial + "password")
                .userType(UserType.NORMAL)
                .addedTracks(new HashSet<>())
                .build();
    }

    public static SpotifyCredential generateSpotifyCredential(final User user) {
        return SpotifyCredential.builder()
                .owner(user)
                .state("1593bead-e671-4a0b-a195-b5165aed6410")
                .token("token")
                .refreshToken("refreshToken")
                .build();
    }

    public static TrackInQueue generateTrackInQueue(final String serial, final Party party,
                                                    final User addedBy, final Set<Artist> artists) {
        return TrackInQueue.builder()
                .party(party)
                .isPlaying(false)
                .title(serial + "title")
                .artists(artists)
                .platformType(PlatformType.SPOTIFY)
                .uri("spotify:track:something" + serial)
                .coverUri("https://some-url" + serial + ".com")
                .addedBy(addedBy)
                .length(1)
                .score(0)
                .build();
    }

    public static PreviousTrack generatePreviousTrack(final String serial, final Party party,
                                                      final User addedBy, final Set<Artist> artists) {

        return PreviousTrack.builder()
                .party(party)
                .title(serial + "title")
                .artists(artists)
                .platformType(PlatformType.SPOTIFY)
                .uri("spotify:track:something" + serial)
                .coverUri("https://some-url" + serial + ".com")
                .addedBy(addedBy)
                .length(1)
                .endedAt(LocalDateTime.of(2023, 12, 19, 17, 40, 59))
                .build();
    }

    public static Artist generateArtist(final String serial) {
        return Artist.builder()
                .name("artist" + serial)
                .tracks(new HashSet<>())
                .build();
    }

    public static Party generateParty(final String serial, final Set<User> participants) {
        return Party.builder()
                .name("party" + serial)
                .tracksInQueue(new HashSet<>())
                .previousTracks(new HashSet<>())
                .participants(new HashSet<>(participants))
                .build();
    }

    public static UserInPartyResponse generateUserInPartyResponse() {
        return UserInPartyResponse.builder()
                .id(1)
                .username("username")
                .partyRole(PartyRole.PARTICIPANT)
                .build();
    }

    public static ArtistResponse generateArtistResponse() {
        return ArtistResponse.builder()
                .name("artist")
                .build();
    }

    public static UserInPartyTrackInQueueResponse generateUserInPartyTrackInQueueResponse() {
        return UserInPartyTrackInQueueResponse.builder()
                .username("username")
                .build();
    }
}
