package partydj.backend.rest.service;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import partydj.backend.rest.entity.Artist;
import partydj.backend.rest.entity.Party;
import partydj.backend.rest.entity.TrackInQueue;
import partydj.backend.rest.entity.User;
import partydj.backend.rest.entity.error.ThirdPartyApiException;
import partydj.backend.rest.entity.response.TrackSearchResultResponse;
import partydj.backend.rest.mapper.TrackMapper;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Service
public class YouTubeService {
    @Autowired
    private TrackMapper trackMapper;

    @Autowired
    private ArtistService artistService;

    @Autowired
    private TrackService trackService;

    @Value("${app.platforms.youtube.apiKey}")
    private String apiKey;

    private final YouTube youTube;

    public YouTubeService() {
        youTube = new YouTube.Builder(
                new NetHttpTransport(),
                new GsonFactory(),
                httpRequest -> {})
            .setApplicationName("PartyDJ")
            .build();
    }

    public Collection<TrackSearchResultResponse> search(
            final String query, final int offset, final int limit) {

        try {
            YouTube.Search.List search = youTube.search().list(List.of("snippet"));

            search.setKey(apiKey.replace("\"", ""));
            search.setQ(query);
            search.setSafeSearch("none");
            search.setType(List.of("video"));
            search.setFields("items(snippet(title,channelTitle,thumbnails/high/url),id)");
            search.setMaxResults(10L); // TODO: make work with limit & offset params

            final SearchListResponse searchResponse = search.execute();
            final List<SearchResult> searchResultList = searchResponse.getItems();

            return searchResultList
                    .stream()
                    .map(video -> trackMapper.mapYouTubeVideoToTrackSearchResultResponse(video)).toList();

        } catch (IOException e) {
            throw new ThirdPartyApiException("YouTube [search]: " + e.getMessage());
        }
    }

    public TrackInQueue fetchAndSafeTrackInfo(final User loggedInUser, final String uri, final Party party) {
        try {
            YouTube.Videos.List searchVideo = youTube.videos().list(List.of("contentDetails"));
            searchVideo.setKey(apiKey.replace("\"", ""));
            searchVideo.setPart(List.of("snippet", "contentDetails", "id"));
            searchVideo.setId(List.of(uri));
            searchVideo.setFields("items(id,snippet(title,channelTitle,thumbnails(high(url))),contentDetails(duration))");

            final VideoListResponse videoList = searchVideo.execute();
            final Video video = videoList.getItems().get(0);

            HashSet<Artist> uploaders = saveUploader(video.getSnippet().getChannelTitle());

            TrackInQueue track = trackMapper.mapYouTubeVideoToTrack(video, loggedInUser, party, uploaders);
            trackService.save(track);
            uploaders.forEach(uploader -> uploader.addTrack(track));
            artistService.saveAll(uploaders);

            return track;
        } catch (IOException e) {
            throw new ThirdPartyApiException("YouTube [fetchAndSafeTrackInfo]: " + e.getMessage());
        }
    }

    private HashSet<Artist> saveUploader(final String uploaderName) {
        HashSet<Artist> artists = new HashSet<>(artistService.findAllByNameIn(List.of(uploaderName)));

        List<String> dbNames = artists.stream().map(Artist::getName).toList();

        if (!dbNames.contains(uploaderName)) {
            artists.add(artistService.register(
                    Artist.builder()
                            .name(uploaderName)
                            .tracks(new HashSet<>())
                            .build()));
        }
        return artists;
    }
}
