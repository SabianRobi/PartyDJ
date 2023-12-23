package partydj.backend.rest.entity.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

public class UserInPartyTrackInQueueResponseTest {
    private final UserInPartyTrackInQueueResponse userInPartyTrackInQueueResponse;
    private final ObjectMapper objectMapper;
    private final String path;

    private UserInPartyTrackInQueueResponseTest() {
        userInPartyTrackInQueueResponse = UserInPartyTrackInQueueResponse.builder()
                .username("username")
                .build();
        objectMapper = new ObjectMapper();
        path = "classpath:domain/response/userInPartyTrackInQueueResponse.json";
    }

    @Test
    @SneakyThrows
    void shouldSerialize() {
        final String actual = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(userInPartyTrackInQueueResponse);

        final File jsonFile = ResourceUtils.getFile(path);
        final String expected = Files.readString(jsonFile.toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    void shouldDeserialize() {
        final UserInPartyTrackInQueueResponse actual = objectMapper.readValue(
                ResourceUtils.getFile(path), UserInPartyTrackInQueueResponse.class);

        assertThat(actual).isEqualTo(userInPartyTrackInQueueResponse);
    }
}
