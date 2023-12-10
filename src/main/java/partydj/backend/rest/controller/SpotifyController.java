package partydj.backend.rest.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.neovisionaries.i18n.CountryCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.apache.hc.core5.http.ParseException;
import org.hibernate.validator.constraints.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import partydj.backend.rest.domain.*;
import partydj.backend.rest.domain.error.ThirdPartyApiException;
import partydj.backend.rest.domain.response.SpotifyCredentialResponse;
import partydj.backend.rest.domain.response.SpotifyLoginUriResponse;
import partydj.backend.rest.domain.response.TrackSearchResultResponse;
import partydj.backend.rest.mapper.SpotifyCredentialMapper;
import partydj.backend.rest.mapper.TrackMapper;
import partydj.backend.rest.service.ArtistService;
import partydj.backend.rest.service.SpotifyCredentialService;
import partydj.backend.rest.service.TrackService;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.requests.data.player.StartResumeUsersPlaybackRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

import java.io.IOException;
import java.net.URI;
import java.util.*;

@RestController
@RequestMapping(value = "/api/v1/platforms/spotify", produces = "application/json")
public class SpotifyController {

    @Autowired
    private SpotifyCredentialService spotifyCredentialService;

    @Autowired
    private SpotifyCredentialMapper spotifyCredentialMapper;

    @Autowired
    private TrackMapper trackMapper;

    @Autowired
    private ArtistService artistService;

    @Autowired
    private TrackService trackService;

    private final SpotifyApi spotifyApi;

    @Autowired
    public SpotifyController(final Map<String, String> spotifyConfigs) {
        spotifyApi = new SpotifyApi.Builder()
                .setClientId(spotifyConfigs.get("client-id"))
                .setClientSecret(spotifyConfigs.get("client-secret"))
                .setRedirectUri(URI.create(spotifyConfigs.get("redirect-uri")))
                .build();
    }

    @GetMapping("/login")
    public SpotifyLoginUriResponse getLoginURI(final Authentication auth) {
        return new SpotifyLoginUriResponse(spotifyCredentialService.getLoginUri(auth));
    }

    @GetMapping("/callback")
    @ResponseStatus(HttpStatus.CREATED)
    public SpotifyCredentialResponse processCallback(@RequestParam @NotNull @NotBlank final String code,
                                                     @RequestParam @NotNull @UUID final java.util.UUID state) {
        final SpotifyCredential spotifyCredential = spotifyCredentialService.processCallback(code, state);

        return spotifyCredentialMapper.mapCredentialToCredentialResponse(spotifyCredential);
    }

    @PostMapping("/logout")
    public SpotifyCredentialResponse logout(final Authentication auth) {
        final SpotifyCredential spotifyCredential = spotifyCredentialService.logout(auth);
        return spotifyCredentialMapper.mapCredentialToCredentialResponse(spotifyCredential);
    }

    @GetMapping("/token")
    public SpotifyCredentialResponse getToken(final Authentication auth) {
        final SpotifyCredential spotifyCredential = spotifyCredentialService.getToken(auth);

        return spotifyCredentialMapper.mapCredentialToCredentialResponse(spotifyCredential);
    }

    @PatchMapping("/token")
    public SpotifyCredentialResponse refreshToken(final Authentication auth) {
        final SpotifyCredential spotifyCredential = spotifyCredentialService.refreshToken(auth);

        return spotifyCredentialMapper.mapCredentialToCredentialResponse(spotifyCredential);
    }

    protected Collection<TrackSearchResultResponse> search(final String query, final int offset,
                                                           final int limit, final User loggedInUser) {
        spotifyApi.setAccessToken(loggedInUser.getSpotifyCredential().getToken());
        SearchTracksRequest searchTracksRequest = spotifyApi.searchTracks(query)
                .market(CountryCode.HU)
                .offset(offset)
                .limit(limit)
                .build();

        Paging<se.michaelthelin.spotify.model_objects.specification.Track> trackPaging;

        try {
            trackPaging = searchTracksRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new ThirdPartyApiException("Failed to search tracks: " + e.getMessage());
        }

        return Arrays.stream(trackPaging.getItems()).map(track ->
                trackMapper.mapSpotifyTrackToTrackSearchResultResponse(track)).toList();
    }

    protected TrackInQueue fetchAndSafeTrackInfo(final String uri, final User loggedInUser, final Party party) {
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

        artists.forEach(artist -> {
            artist.addTrack(track);
            artistService.save(artist);
        });

        return track;
    }

    protected void playNextTrack(final Party party, final TrackInQueue track, final User loggedInUser) {
        spotifyApi.setAccessToken(loggedInUser.getSpotifyCredential().getToken());

        JsonArray jsonUri = JsonParser.parseString("[\"" + track.getUri() + "\"]").getAsJsonArray();

        StartResumeUsersPlaybackRequest startResumeUsersPlaybackRequest = spotifyApi
                .startResumeUsersPlayback()
                .device_id(party.getSpotifyDeviceId())
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
