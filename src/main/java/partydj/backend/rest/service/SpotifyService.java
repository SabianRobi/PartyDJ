package partydj.backend.rest.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.neovisionaries.i18n.CountryCode;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import partydj.backend.rest.entity.*;
import partydj.backend.rest.entity.error.ThirdPartyApiException;
import partydj.backend.rest.entity.response.TrackSearchResultResponse;
import partydj.backend.rest.mapper.TrackMapper;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.data.player.StartResumeUsersPlaybackRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

import java.io.IOException;
import java.net.URI;
import java.util.*;

@Service
public class SpotifyService {

    @Autowired
    private TrackMapper trackMapper;

    @Autowired
    private ArtistService artistService;

    @Autowired
    private TrackService trackService;

    @Lazy
    @Autowired
    private SpotifyCredentialService spotifyCredentialService;

    @Autowired
    private Map<String, String> spotifyConfigs;

    private final SpotifyApi spotifyApi;

    @Autowired
    public SpotifyService(final Map<String, String> spotifyConfigs) {
        spotifyApi = new SpotifyApi.Builder()
                .setClientId(spotifyConfigs.get("client-id"))
                .setClientSecret(spotifyConfigs.get("client-secret"))
                .setRedirectUri(URI.create(spotifyConfigs.get("redirect-uri")))
                .build();
    }

    public URI generateLoginUri(final String state) {
        final AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                .scope(spotifyConfigs.get("scopes"))
                .show_dialog(true)
                .state(state)
                .build();
        return authorizationCodeUriRequest.execute();
    }

    public SpotifyCredential processCallback(final SpotifyCredential spotifyCredential, final String code) {
        final AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code).build();

        try {
            // Get user token
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

            // Save tokens
            spotifyCredential.setToken(authorizationCodeCredentials.getAccessToken());
            spotifyCredential.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
            spotifyCredential.setState(null);

            spotifyCredentialService.save(spotifyCredential);
        } catch (IOException | SpotifyWebApiException | ParseException ex) {
            throw new ThirdPartyApiException("Failed to log in with Spotify: " + ex.getMessage());
        }

        return spotifyCredential;
    }

    public SpotifyCredential refreshToken(final SpotifyCredential spotifyCredential) {
        spotifyApi.setRefreshToken(spotifyCredential.getRefreshToken());
        final AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest =
                spotifyApi.authorizationCodeRefresh().build();
        try {
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();

            spotifyCredential.setToken(authorizationCodeCredentials.getAccessToken());
            spotifyCredentialService.save(spotifyCredential);
        } catch (IOException | SpotifyWebApiException | ParseException ex) {
            throw new ThirdPartyApiException("Failed to refresh Spotify token: " + ex.getMessage());
        }
        return spotifyCredential;
    }


    public Collection<TrackSearchResultResponse> search(final User loggedInUser, final String query,
                                                        final int offset, final int limit) {
        spotifyApi.setAccessToken(loggedInUser.getSpotifyCredential().getToken());
        SearchTracksRequest searchTracksRequest = spotifyApi.searchTracks(query)
                .market(CountryCode.HU)
                .offset(offset)
                .limit(limit)
                .build();

        Paging<Track> trackPaging;

        try {
            trackPaging = searchTracksRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new ThirdPartyApiException("Failed to search tracks: " + e.getMessage());
        }

        return Arrays.stream(trackPaging.getItems()).map(track ->
                trackMapper.mapSpotifyTrackToTrackSearchResultResponse(track)).toList();
    }

    public TrackInQueue fetchAndSafeTrackInfo(final User loggedInUser, final String uri, final Party party) {
        spotifyApi.setAccessToken(loggedInUser.getSpotifyCredential().getToken());
        GetTrackRequest getTrackRequest = spotifyApi.getTrack(uri.split(":")[2])
                .market(CountryCode.HU)
                .build();
        se.michaelthelin.spotify.model_objects.specification.Track spotifyTrack;

        try {
            spotifyTrack = getTrackRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new ThirdPartyApiException("Failed to fetch track infos: " + e.getMessage());
        }

        HashSet<Artist> artists = saveArtists(
                Arrays.stream(spotifyTrack.getArtists())
                        .map(ArtistSimplified::getName)
                        .toList());

        TrackInQueue track = trackMapper.mapSpotifyTrackToTrack(spotifyTrack, loggedInUser, party, artists);
        trackService.save(track);

        artists.forEach(artist -> artist.addTrack(track));
        artistService.saveAll(artists);

        return track;
    }

    public void playNextTrack(final Party party, final TrackInQueue track, final User loggedInUser) {
        spotifyApi.setAccessToken(loggedInUser.getSpotifyCredential().getToken());

        JsonArray jsonUri = JsonParser.parseString("[\"" + track.getUri() + "\"]").getAsJsonArray();

        StartResumeUsersPlaybackRequest startResumeUsersPlaybackRequest = spotifyApi
                .startResumeUsersPlayback()
//                .device_id(party.getSpotifyDeviceId()) //TODO: UNCOMMENT AFTER DEMO
                .uris(jsonUri)
                .build();

        try {
            startResumeUsersPlaybackRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new ThirdPartyApiException("Could not play next track: " + e.getMessage());
        }
    }

    private HashSet<Artist> saveArtists(final List<String> artistNames) {
        HashSet<Artist> artists = new HashSet<>(artistService.findAllByNameIn(artistNames));

        List<String> dbNames = artists.stream().map(Artist::getName).toList();

        artistNames.forEach(artistName -> {
            if (!dbNames.contains(artistName)) {
                artists.add(artistService.register(
                        Artist.builder()
                                .name(artistName)
                                .tracks(new HashSet<>())
                                .build()));
            }
        });
        return artists;
    }
}
