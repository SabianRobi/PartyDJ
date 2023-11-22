package partydj.backend.rest.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import partydj.backend.rest.domain.*;
import partydj.backend.rest.domain.enums.PlatformType;
import partydj.backend.rest.domain.response.PreviousTrackResponse;
import partydj.backend.rest.domain.response.TrackInQueueResponse;
import partydj.backend.rest.domain.response.TrackSearchResultResponse;
import partydj.backend.rest.service.ArtistService;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;

import java.time.LocalDateTime;
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
                .artists(Arrays.stream(track.getArtists()).map(ArtistSimplified::getName).toList())
                .coverUri(track.getAlbum().getImages()[1].getUrl())
                .length(track.getDurationMs())
                .platformType(PlatformType.SPOTIFY)
                .uri(track.getUri())
                .build();
    }

    public TrackInQueue mapSpotifyTrackToTrack(final se.michaelthelin.spotify.model_objects.specification.Track track,
                                               final User addedBy, final Party party) {
        Collection<Artist> artists = new ArrayList<>();
        Arrays.stream(track.getArtists()).forEach(artist -> {
            Artist newArtist = Artist.builder()
                    .name(artist.getName())
                    .build();
            artists.add(artistService.register(newArtist));
        });

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

    public PreviousTrack mapTrackInQueueToPreviousTrack(final TrackInQueue track) {
        ArrayList<Artist> newArtists = new ArrayList<>();
        track.getArtists().forEach(artist -> {
            Artist newArtist = Artist.builder()
                    .name(artist.getName())
                    .build();
            artistService.save(newArtist);
            newArtists.add(newArtist);
        });

        return PreviousTrack.builder()
                .uri(track.getUri())
                .title(track.getTitle())
                .coverUri(track.getCoverUri())
                .length(track.getLength())
                .artists(newArtists)
                .platformType(track.getPlatformType())
                .addedBy(track.getAddedBy())
                .party(track.getParty())
                .endedAt(LocalDateTime.now())
                .build();
    }
}
