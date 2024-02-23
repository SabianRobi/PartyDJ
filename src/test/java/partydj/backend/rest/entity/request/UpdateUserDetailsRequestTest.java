package partydj.backend.rest.entity.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateUserDetailsRequestTest {
    private final UpdateUserDetailsRequest updateUserDetailsRequest;
    private final ObjectMapper objectMapper;
    private final String path;

    private UpdateUserDetailsRequestTest() {
        updateUserDetailsRequest = UpdateUserDetailsRequest.builder()
                .email("test@user.co")
                .username("testUser")
                .build();
        objectMapper = new ObjectMapper();
        path = "classpath:domain/request/updateUserDetailsRequest.json";
    }

    @Test
    @SneakyThrows
    void shouldSerialize() {
        final String actual = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(updateUserDetailsRequest);

        final File jsonFile = ResourceUtils.getFile(path);
        final String expected = Files.readString(jsonFile.toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    void shouldDeserialize() {
        final UpdateUserDetailsRequest actual = objectMapper.readValue(
                ResourceUtils.getFile(path), UpdateUserDetailsRequest.class);

        assertThat(actual).isEqualTo(updateUserDetailsRequest);
    }
}
