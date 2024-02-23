package partydj.backend.rest.entity.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateUserPasswordRequestTest {
    private final UpdateUserPasswordRequest updateUserPasswordRequest;
    private final ObjectMapper objectMapper;
    private final String path;

    private UpdateUserPasswordRequestTest() {
        updateUserPasswordRequest = UpdateUserPasswordRequest.builder()
                .currentPassword("testPassword")
                .password("newTestPassword")
                .confirmPassword("newTestPassword")
                .build();
        objectMapper = new ObjectMapper();
        path = "classpath:domain/request/updateUserPasswordRequest.json";
    }

    @Test
    @SneakyThrows
    void shouldSerialize() {
        final String actual = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(updateUserPasswordRequest);

        final File jsonFile = ResourceUtils.getFile(path);
        final String expected = Files.readString(jsonFile.toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    void shouldDeserialize() {
        final UpdateUserPasswordRequest actual = objectMapper.readValue(
                ResourceUtils.getFile(path), UpdateUserPasswordRequest.class);

        assertThat(actual).isEqualTo(updateUserPasswordRequest);
    }
}
