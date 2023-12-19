package partydj.backend.rest.domain.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;
import partydj.backend.rest.domain.enums.PartyRole;

import java.io.File;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

public class UserInPartyResponseTest {
    private final UserInPartyResponse userInPartyResponse;
    private final ObjectMapper objectMapper;
    private final String path;

    private UserInPartyResponseTest() {
        userInPartyResponse = UserInPartyResponse.builder()
                .id(1)
                .username("username")
                .partyRole(PartyRole.PARTICIPANT)
                .build();
        objectMapper = new ObjectMapper();
        path = "classpath:domain/response/userInPartyResponse.json";
    }

    @Test
    @SneakyThrows
    void shouldSerialize() {
        final String actual = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(userInPartyResponse);

        final File jsonFile = ResourceUtils.getFile(path);
        final String expected = Files.readString(jsonFile.toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    void shouldDeserialize() {
        final UserInPartyResponse actual = objectMapper.readValue(
                ResourceUtils.getFile(path), UserInPartyResponse.class);

        assertThat(actual).isEqualTo(userInPartyResponse);
    }
}
