package partydj.backend.rest.domain.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;
import partydj.backend.rest.domain.enums.PlatformType;
import partydj.backend.rest.helper.DataGenerator;

import java.io.File;
import java.nio.file.Files;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class TrackInQueueResponseTest {
    private final TrackInQueueResponse trackInQueueResponse;
    private final ObjectMapper objectMapper;
    private final String path;

    private TrackInQueueResponseTest() {
        final ArtistResponse artistResponse = DataGenerator.generateArtistResponse();
        final UserInPartyTrackInQueueResponse userResponse = DataGenerator.generateUserInPartyTrackInQueueResponse();

        trackInQueueResponse = TrackInQueueResponse.builder()
                .id(1)
                .title("title")
                .artists(Set.of(artistResponse))
                .coverUri("https://cover.uri")
                .length(420)
                .platformType(PlatformType.SPOTIFY)
                .addedBy(userResponse)
                .build();
        objectMapper = new ObjectMapper();
        path = "classpath:domain/response/trackInQueueResponse.json";
    }

    @Test
    @SneakyThrows
    void shouldSerialize() {
        final String actual = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(trackInQueueResponse);

        final File jsonFile = ResourceUtils.getFile(path);
        final String expected = Files.readString(jsonFile.toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    void shouldDeserialize() {
        final TrackInQueueResponse actual = objectMapper.readValue(
                ResourceUtils.getFile(path), TrackInQueueResponse.class);

        assertThat(actual).isEqualTo(trackInQueueResponse);
    }
}
