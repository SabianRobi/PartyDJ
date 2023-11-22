package partydj.backend.rest.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import partydj.backend.rest.domain.Artist;
import partydj.backend.rest.domain.Track;
import partydj.backend.rest.domain.User;
import partydj.backend.rest.domain.enums.PlatformType;
import partydj.backend.rest.domain.response.PreviousTrackResponse;
import partydj.backend.rest.domain.response.TrackInQueueResponse;
import partydj.backend.rest.domain.response.TrackSearchResultResponse;
import partydj.backend.rest.service.ArtistService;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@Component
public class TrackMapper {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ArtistService artistService;

    @Autowired
    private ArtistMapper artistMapper;

    public TrackInQueueResponse mapTrackToTrackInQueueResponse(final Track track) {
        return TrackInQueueResponse.builder()
                .id(track.getId())
                .title(track.getTitle())
                .artists(track.getArtists().stream().map(artist ->
                        artistMapper.mapArtistToArtistResponse(artist)).toList())
                .coverUri(track.getCoverUri())
                .length(track.getLength())
                .platformType(track.getPlatformType())
                .addedBy(userMapper.mapUserToUserInPartyTrackInQueueResponse(track.getAddedBy()))
                .build();
    }

    public PreviousTrackResponse mapTrackToPreviousTrackResponse(final Track track) {
        return PreviousTrackResponse.builder()
                .title(track.getTitle())
                .artists(track.getArtists().stream().map(artist ->
                        artistMapper.mapArtistToArtistResponse(artist)).toList())
                .length(track.getLength())
                .platformType(track.getPlatformType())
                .addedBy(userMapper.mapUserToUserInPartyTrackInQueueResponse(track.getAddedBy()))
                .build();
    }

    public TrackSearchResultResponse mapSpotifyTrackToTrackSearchResultResponse(
            final se.michaelthelin.spotify.model_objects.specification.Track track) {
        return TrackSearchResultResponse.builder()
                .title(track.getName())
                .artists(Arrays.stream(track.getArtists()).map(ArtistSimplified::getName).toList())
                .coverUri(track.getAlbum().getImages()[1].getUrl())
                .length(track.getDurationMs())
                .platformType(PlatformType.SPOTIFY)
                .uri(track.getUri())
                .build();
    }

    public Track mapSpotifyTrackToTrack(final se.michaelthelin.spotify.model_objects.specification.Track track,
                                        final User addedBy) {
        Collection<Artist> artists = new ArrayList<>();
        Arrays.stream(track.getArtists()).forEach(artist -> {
            Artist newArtist = Artist.builder()
                    .name(artist.getName())
                    .build();
            artists.add(artistService.register(newArtist));
        });

        return Track.builder()
                .uri(track.getUri())
                .title(track.getName())
                .artists(artists)
                .coverUri(track.getAlbum().getImages()[1].getUrl())
                .length(track.getDurationMs())
                .score(0)
                .platformType(PlatformType.SPOTIFY)
                .addedBy(addedBy)
                .build();
    }
}
