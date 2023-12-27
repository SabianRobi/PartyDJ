package partydj.backend.rest.helper;

import org.springframework.stereotype.Component;
import partydj.backend.rest.entity.*;
import partydj.backend.rest.entity.enums.PlatformType;
import partydj.backend.rest.entity.enums.UserType;
import partydj.backend.rest.entity.request.AddTrackRequest;
import partydj.backend.rest.entity.request.PartyRequest;
import partydj.backend.rest.entity.request.SetSpotifyDeviceIdRequest;
import partydj.backend.rest.entity.request.UserRequest;
import partydj.backend.rest.entity.response.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class DataGenerator {

    public static User generateUser() {
        return generateUser("");
    }

    public static User generateUser(final String serial) {
        User user = generateUserWithoutId(serial);
        user.setId(1);
        return user;
    }

    public static User generateUserWithoutId(final String serial) {
        return User.builder()
                .email(serial + "user@test.com")
                .username(serial + "username")
                .password(serial + "password")
                .userType(UserType.NORMAL)
                .addedTracks(new HashSet<>())
                .build();
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
        TrackInQueue track = generateTrackInQueueWithoutId(serial, party, addedBy, artists);
        track.setId(1);
        return track;
    }

    public static TrackInQueue generateTrackInQueueWithoutId(final String serial, final Party party,
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
                .id(1)
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
        Artist artist = generateArtistWithoutId(serial);
        artist.setId(1);
        return artist;
    }

    public static Artist generateArtist() {
        Artist artist = generateArtistWithoutId("");
        artist.setId(1);
        return artist;
    }

    public static Artist generateArtistWithoutId(final String serial) {
        return Artist.builder()
                .name(serial + "artist")
                .tracks(new HashSet<>())
                .build();
    }

    public static Party generateParty(final String serial, final Set<User> participants) {
        Party party = generatePartyWithoutId(serial, participants);
        party.setId(1);
        return party;
    }

    public static Party generatePartyWithoutId(final String serial, final Set<User> participants) {
        return Party.builder()
                .name("party" + serial)
                .password("password")
                .tracksInQueue(new HashSet<>())
                .previousTracks(new HashSet<>())
                .participants(new HashSet<>(participants))
                .spotifyDeviceId("deviceId")
                .build();
    }


    // REQUESTS

    public static UserRequest generateUserRequest(final User user) {
        return UserRequest.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }

    public static PartyRequest generatePartyRequest(final Party party) {
        return PartyRequest.builder()
                .name(party.getName())
                .password(party.getPassword())
                .build();
    }

    public static TrackSearchResultResponse generateTrackSearchResultResponse(final Set<Artist> artists,
                                                                              final TrackInQueue track) {
        return TrackSearchResultResponse.builder()
                .uri(track.getUri())
                .title(track.getTitle())
                .artists(artists.stream().map(Artist::getName).collect(Collectors.toSet()))
                .coverUri(track.getCoverUri())
                .length(track.getLength())
                .platformType(track.getPlatformType())
                .build();
    }

    public static AddTrackRequest generateAddTrackRequest(final Track track) {
        return AddTrackRequest.builder()
                .uri(track.getUri())
                .platformType(track.getPlatformType())
                .build();
    }

    public static SetSpotifyDeviceIdRequest generateSpotifyRequest(final Party party) {
        return SetSpotifyDeviceIdRequest.builder()
                .deviceId(party.getSpotifyDeviceId())
                .build();
    }

    public static SpotifyCredential generateSpotifyCredentialWithOnlyState(final User user) {
        return SpotifyCredential.builder()
                .owner(user)
                .state(UUID.randomUUID().toString())
                .build();
    }


    // RESPONSES


    public static ArtistResponse generateArtistResponse(final Artist artist) {
        return ArtistResponse.builder()
                .name(artist.getName())
                .build();
    }

    public static PartyResponse generatePartyResponse(final Party party,
                                                      final Set<TrackInQueueResponse> trackResponses,
                                                      final Set<UserInPartyResponse> userResponses) {
        return PartyResponse.builder()
                .id(party.getId())
                .name(party.getName())
                .tracksInQueue(trackResponses)
                .participants(userResponses)
                .build();
    }

    public static TrackInQueueResponse generateTrackInQueueResponse(final TrackInQueue track,
                                                                    final Set<ArtistResponse> artistResponses,
                                                                    final UserInPartyTrackInQueueResponse userResponse) {
        return TrackInQueueResponse.builder()
                .id(track.getId())
                .title(track.getTitle())
                .artists(artistResponses)
                .coverUri(track.getCoverUri())
                .length(track.getLength())
                .platformType(track.getPlatformType())
                .addedBy(userResponse)
                .build();
    }

    public static UserInPartyResponse generateUserInPartyResponse(final User user) {
        return UserInPartyResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .partyRole(user.getPartyRole())
                .build();
    }

    public static UserInPartyTrackInQueueResponse generateUserInPartyTrackInQueueResponse(final User user) {
        return UserInPartyTrackInQueueResponse.builder()
                .username(user.getUsername())
                .build();
    }

    public static SpotifyDeviceIdResponse generateSpotifyDeviceIdResponse(final Party party) {
        return new SpotifyDeviceIdResponse(party.getSpotifyDeviceId());
    }

    public static PreviousTrackResponse generatePreviousTrackResponse(final PreviousTrack previousTrack,
                                                                      final Set<ArtistResponse> artistResponses,
                                                                      final UserInPartyTrackInQueueResponse userResponse) {
        return PreviousTrackResponse.builder()
                .title(previousTrack.getTitle())
                .coverUri(previousTrack.getCoverUri())
                .length(previousTrack.getLength())
                .artists(artistResponses)
                .platformType(previousTrack.getPlatformType())
                .addedBy(userResponse)
                .endedAt(previousTrack.getEndedAt())
                .build();
    }

    public static SpotifyCredentialResponse generateSpotifyCredentialResponse(final SpotifyCredential spotifyCredential) {
        return SpotifyCredentialResponse.builder()
                .token(spotifyCredential.getToken())
                .build();
    }

    public static SpotifyLoginUriResponse generateSpotifyLoginUriResponse(final URI loginUri) {
        return new SpotifyLoginUriResponse(loginUri.toString());
    }

    public static UserResponse generateUserResponse(final User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .isSpotifyConnected(user.getSpotifyCredential() != null)
                .build();
    }
}
