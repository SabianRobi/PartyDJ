package partydj.backend.rest.mapper;

import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import partydj.backend.rest.entity.*;
import partydj.backend.rest.entity.enums.PlatformType;
import partydj.backend.rest.entity.response.PreviousTrackResponse;
import partydj.backend.rest.entity.response.TrackInQueueResponse;
import partydj.backend.rest.entity.response.TrackSearchResultResponse;
import partydj.backend.rest.service.ArtistService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;

@Component
public class TrackMapper {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ArtistService artistService;

    @Autowired
    private ArtistMapper artistMapper;

    public TrackInQueueResponse mapTrackToTrackInQueueResponse(final TrackInQueue track) {
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

    public PreviousTrackResponse mapPreviousTrackToPreviousTrackResponse(final PreviousTrack track) {
        return PreviousTrackResponse.builder()
                .title(track.getTitle())
                .coverUri(track.getCoverUri())
                .length(track.getLength())
                .artists(track.getArtists().stream().map(artist ->
                        artistMapper.mapArtistToArtistResponse(artist)).toList())
                .platformType(track.getPlatformType())
                .addedBy(userMapper.mapUserToUserInPartyTrackInQueueResponse(track.getAddedBy()))
                .endedAt(track.getEndedAt())
                .build();
    }

    public TrackSearchResultResponse mapSpotifyTrackToTrackSearchResultResponse(
            final se.michaelthelin.spotify.model_objects.specification.Track track) {
        return TrackSearchResultResponse.builder()
                .title(track.getName())
                .artists(Arrays.stream(track.getArtists()).map(artistSimplified ->
                        artistMapper.mapSimplifiedArtistToArtistResponse(artistSimplified)).collect(Collectors.toSet()))
                .coverUri(track.getAlbum().getImages()[1].getUrl())
                .length(track.getDurationMs())
                .platformType(PlatformType.SPOTIFY)
                .uri(track.getUri())
                .build();
    }

    public TrackSearchResultResponse mapYouTubeVideoToTrackSearchResultResponse(final SearchResult video) {
        return TrackSearchResultResponse.builder()
                .title(video.getSnippet().getTitle())
                .artists(new HashSet<>(Collections.singletonList(artistMapper.mapYouTubeSearchResultToArtistResponse(video))))
                .coverUri(video.getSnippet().getThumbnails().getHigh().getUrl())
                .length(0) // TODO: get this data
                .platformType(PlatformType.YOUTUBE)
                .uri(video.getId().getVideoId())
                .build();
    }

    public TrackInQueue mapSpotifyTrackToTrack(final se.michaelthelin.spotify.model_objects.specification.Track track,
                                               final User addedBy, final Party party, final HashSet<Artist> artists) {

        return TrackInQueue.builder()
                .uri(track.getUri())
                .title(track.getName())
                .artists(artists)
                .coverUri(track.getAlbum().getImages()[1].getUrl())
                .length(track.getDurationMs())
                .score(0)
                .platformType(PlatformType.SPOTIFY)
                .addedBy(addedBy)
                .isPlaying(false)
                .party(party)
                .build();
    }

    public TrackInQueue mapYouTubeVideoToTrack(Video video, final User addedBy,
                                               final Party party, final HashSet<Artist> artists) {
        final int duration = (int) Duration.parse(video.getContentDetails().getDuration()).toMillis();

        return TrackInQueue.builder()
                .uri(video.getId())
                .title(video.getSnippet().getTitle())
                .artists(artists)
                .coverUri(video.getSnippet().getThumbnails().getHigh().getUrl())
                .length(duration)
                .score(0)
                .platformType(PlatformType.YOUTUBE)
                .addedBy(addedBy)
                .isPlaying(false)
                .party(party)
                .build();
    }

    public PreviousTrack mapTrackInQueueToPreviousTrack(final TrackInQueue track) {
        HashSet<Artist> artists = new HashSet<>(artistService.findAllByTracksContaining(track));

        return PreviousTrack.builder()
                .uri(track.getUri())
                .title(track.getTitle())
                .coverUri(track.getCoverUri())
                .length(track.getLength())
                .artists(artists)
                .platformType(track.getPlatformType())
                .addedBy(track.getAddedBy())
                .party(track.getParty())
                .endedAt(LocalDateTime.now())
                .build();
    }
}
