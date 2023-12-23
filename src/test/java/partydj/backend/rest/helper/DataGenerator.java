package partydj.backend.rest.helper;

import org.springframework.stereotype.Component;
import partydj.backend.rest.domain.*;
import partydj.backend.rest.domain.enums.PartyRole;
import partydj.backend.rest.domain.enums.PlatformType;
import partydj.backend.rest.domain.enums.UserType;
import partydj.backend.rest.domain.request.AddTrackRequest;
import partydj.backend.rest.domain.request.PartyRequest;
import partydj.backend.rest.domain.request.SetSpotifyDeviceIdRequest;
import partydj.backend.rest.domain.request.UserRequest;
import partydj.backend.rest.domain.response.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class DataGenerator {

    public static User generateUser(final String serial) {
        return User.builder()
                .email(serial + "user@test.com")
                .username(serial + "user")
                .password(serial + "password")
                .userType(UserType.NORMAL)
                .addedTracks(new HashSet<>())
                .build();
    }

    public static User generateUserWithId() {
        User user = generateUser("");
        user.setId(1);
        return user;
    }

    public static User generateUserWithId(final String serial) {
        User user = generateUser(serial);
        user.setId(1);
        return user;
    }

    public static SpotifyCredential generateSpotifyCredential(final User user, final String serial) {
        final SpotifyCredential spotifyCredential = generateSpotifyCredential(user);
        spotifyCredential.setToken(serial + spotifyCredential.getToken());
        return spotifyCredential;
    }

    public static SpotifyCredential generateSpotifyCredential(final User user) {
        return SpotifyCredential.builder()
                .owner(user)
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

    public static TrackInQueue generateTrackInQueueWithId(final String serial, final Party party,
                                                          final User addedBy, final Set<Artist> artists) {
        final TrackInQueue track = generateTrackInQueue(serial, party, addedBy, artists);
        track.setId(1);
        return track;
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

    public static Artist generateArtist() {
        return generateArtist("");
    }

    public static Artist generateArtist(final String serial) {
        return Artist.builder()
                .name(serial + "artist")
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

    public static UserRequest generateUserRequest() {
        return UserRequest.builder()
                .email("user@test.com")
                .username("username")
                .password("password")
                .build();
    }

    public static PartyRequest generatePartyRequest() {
        return PartyRequest.builder()
                .name("testParty")
                .password("password")
                .build();
    }

    public static TrackSearchResultResponse generateTrackSearchResultResponse(final Set<Artist> artists) {
        return TrackSearchResultResponse.builder()
                .uri("spotify:track:something")
                .title("title")
                .artists(artists.stream().map(Artist::getName).collect(Collectors.toSet()))
                .coverUri("https://cover.uri")
                .length(420)
                .platformType(PlatformType.SPOTIFY)
                .build();
    }

    public static AddTrackRequest generateAddTrackRequest() {
        return AddTrackRequest.builder()
                .uri("spotify:track:something")
                .platformType(PlatformType.SPOTIFY)
                .build();
    }

    public static SetSpotifyDeviceIdRequest generateSpotifyRequest() {
        return SetSpotifyDeviceIdRequest.builder()
                .deviceId("deviceId")
                .build();
    }

    public static SpotifyCredential generateSpotifyCredentialWithOnlyState(final User user) {
        return SpotifyCredential.builder()
                .owner(user)
                .state(UUID.randomUUID().toString())
                .build();
    }

    public static UserResponse generateUserResponse() {
        return UserResponse.builder()
                .id(1)
                .email("user@test.com")
                .username("username")
                .isSpotifyConnected(false)
                .build();
    }
}
