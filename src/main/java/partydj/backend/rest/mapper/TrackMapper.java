package partydj.backend.rest.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import partydj.backend.rest.domain.Track;
import partydj.backend.rest.domain.enums.PlatformType;
import partydj.backend.rest.domain.response.TrackInQueueResponse;
import partydj.backend.rest.domain.response.TrackSearchResultResponse;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;

import java.util.Arrays;

@Component
public class TrackMapper {

    @Autowired
    UserMapper userMapper;

    public TrackInQueueResponse mapTrackToTrackInQueue(final Track track) {
        return TrackInQueueResponse.builder()
                .id(track.getId())
                .uri(track.getUri())
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
}
