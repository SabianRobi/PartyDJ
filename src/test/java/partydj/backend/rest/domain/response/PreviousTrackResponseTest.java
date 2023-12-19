package partydj.backend.rest.domain.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;
import partydj.backend.rest.domain.enums.PlatformType;
import partydj.backend.rest.helper.DataGenerator;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class PreviousTrackResponseTest {
    private final PreviousTrackResponse previousTrackResponse;
    private final ObjectMapper objectMapper;
    private final String path;

    private PreviousTrackResponseTest() {
        final ArtistResponse artistResponse = DataGenerator.generateArtistResponse();
        final UserInPartyTrackInQueueResponse userResponse = DataGenerator.generateUserInPartyTrackInQueueResponse();

        previousTrackResponse = PreviousTrackResponse.builder()
                .title("title")
                .coverUri("https://cover.uri")
                .length(420)
                .artists(Set.of(artistResponse))
                .platformType(PlatformType.SPOTIFY)
                .addedBy(userResponse)
                .endedAt(LocalDateTime.of(2023, 12, 19, 14, 15, 16))
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        path = "classpath:domain/response/previousTrackResponse.json";
    }

    @Test
    @SneakyThrows
    void shouldSerialize() {
        final String actual = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(previousTrackResponse);

        final File jsonFile = ResourceUtils.getFile(path);
        final String expected = Files.readString(jsonFile.toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    void shouldDeserialize() {
        final PreviousTrackResponse actual = objectMapper.readValue(
                ResourceUtils.getFile(path), PreviousTrackResponse.class);

        assertThat(actual).isEqualTo(previousTrackResponse);
    }
}
