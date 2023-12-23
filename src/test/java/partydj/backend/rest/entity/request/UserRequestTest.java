package partydj.backend.rest.entity.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

public class UserRequestTest {
    private final UserRequest userRequest;
    private final ObjectMapper objectMapper;
    private final String path;

    private UserRequestTest() {
        userRequest = UserRequest.builder()
                .username("testUser")
                .email("test@user.co")
                .password("testPassword")
                .build();
        objectMapper = new ObjectMapper();
        path = "classpath:domain/request/userRequest.json";
    }

    @Test
    @SneakyThrows
    void shouldSerialize() {
        final String actual = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(userRequest);

        final File jsonFile = ResourceUtils.getFile(path);
        final String expected = Files.readString(jsonFile.toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    void shouldDeserialize() {
        final UserRequest actual = objectMapper.readValue(
                ResourceUtils.getFile(path), UserRequest.class);

        assertThat(actual).isEqualTo(userRequest);
    }
}
