package partydj.backend.rest.entity.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

public class PartyRequestTest {
    private final PartyRequest partyRequest;
    private final ObjectMapper objectMapper;
    private final String path;

    private PartyRequestTest() {
        partyRequest = PartyRequest.builder()
                .name("testParty")
                .password("testPassword")
                .build();
        objectMapper = new ObjectMapper();
        path = "classpath:domain/request/partyRequest.json";
    }

    @Test
    @SneakyThrows
    void shouldSerialize() {
        final String actual = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(partyRequest);

        final File jsonFile = ResourceUtils.getFile(path);
        final String expected = Files.readString(jsonFile.toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    void shouldDeserialize() {
        final PartyRequest actual = objectMapper.readValue(
                ResourceUtils.getFile(path), PartyRequest.class);

        assertThat(actual).isEqualTo(partyRequest);
    }
}
