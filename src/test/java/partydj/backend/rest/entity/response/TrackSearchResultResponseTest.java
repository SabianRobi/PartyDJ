package partydj.backend.rest.entity.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;
import partydj.backend.rest.entity.Artist;
import partydj.backend.rest.entity.enums.PlatformType;
import partydj.backend.rest.helper.DataGenerator;

import java.io.File;
import java.nio.file.Files;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

public class TrackSearchResultResponseTest {
    private final TrackSearchResultResponse trackSearchResultResponse;
    private final ObjectMapper objectMapper;
    private final String path;

    private TrackSearchResultResponseTest() {
        final Artist artist = DataGenerator.generateArtist();
        final ArtistResponse artistResponse = DataGenerator.generateArtistResponse(artist);
        final HashSet<ArtistResponse> artistResponses = new HashSet<>();
        artistResponses.add(artistResponse);

        trackSearchResultResponse = TrackSearchResultResponse.builder()
                .uri("spotify:track:something")
                .title("title")
                .artists(artistResponses)
                .coverUri("https://cover.uri")
                .length(420)
                .platformType(PlatformType.SPOTIFY)
                .build();
        objectMapper = new ObjectMapper();
        path = "classpath:domain/response/trackSearchResultResponse.json";
    }

    @Test
    @SneakyThrows
    void shouldSerialize() {
        final String actual = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(trackSearchResultResponse);

        final File jsonFile = ResourceUtils.getFile(path);
        final String expected = Files.readString(jsonFile.toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    void shouldDeserialize() {
        final TrackSearchResultResponse actual = objectMapper.readValue(
                ResourceUtils.getFile(path), TrackSearchResultResponse.class);

        assertThat(actual).isEqualTo(trackSearchResultResponse);
    }
}
