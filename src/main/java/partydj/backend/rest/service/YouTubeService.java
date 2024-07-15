package partydj.backend.rest.service;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import partydj.backend.rest.entity.response.TrackSearchResultResponse;
import partydj.backend.rest.mapper.TrackMapper;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class YouTubeService {
    @Autowired
    private TrackMapper trackMapper;

    @Value("${app.platforms.youtube.apiKey}")
    private String apiKey;

    private final YouTube youTube;

    @Autowired
    public YouTubeService(final Map<String, String> youTubeConfigs) {
        youTube = new YouTube.Builder(
                new NetHttpTransport(),
                new JacksonFactory(),
                httpRequest -> {})
            .setApplicationName("PartyDJ")
            .build();
    }

    public Collection<TrackSearchResultResponse> search(
            final String query, final int offset, final int limit) {

        try {
            YouTube.Search.List search = youTube.search().list("snippet");

            search.setKey(apiKey.replace("\"", ""));
            search.setQ(query);
            search.setSafeSearch("none");
            search.setType("video");
            search.setFields("items(snippet/title,snippet/channelTitle,snippet/thumbnails/high/url,id)");
            search.setMaxResults((long) limit);

            final SearchListResponse searchResponse = search.execute();
            final List<SearchResult> searchResultList = searchResponse.getItems();

            return searchResultList
                    .stream()
                    .map(video -> trackMapper.mapYouTubeVideoToTrackSearchResultResponse(video)).toList();

        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            return null;
        }
    }
}
