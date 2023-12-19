package partydj.backend.rest.domain.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;
import partydj.backend.rest.helper.DataGenerator;

import java.io.File;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class PartyResponseTest {
    private final PartyResponse partyResponse;
    private final ObjectMapper objectMapper;
    private final String path;

    private PartyResponseTest() {
        UserInPartyResponse userInPartyResponse = DataGenerator.generateUserInPartyResponse();
        partyResponse = PartyResponse.builder()
                .id(1)
                .name("testParty")
                .tracksInQueue(new HashSet<>())
                .participants(Set.of(userInPartyResponse))
                .build();
        objectMapper = new ObjectMapper();
        path = "classpath:domain/response/partyResponse.json";
    }

    @Test
    @SneakyThrows
    void shouldSerialize() {
        final String actual = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(partyResponse);

        final File jsonFile = ResourceUtils.getFile(path);
        final String expected = Files.readString(jsonFile.toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    void shouldDeserialize() {
        final PartyResponse actual = objectMapper.readValue(
                ResourceUtils.getFile(path), PartyResponse.class);

        assertThat(actual).isEqualTo(partyResponse);
    }
}
