package partydj.backend.rest.entity.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class ErrorResponseTest {
    private final ErrorResponse errorResponse;
    private final ObjectMapper objectMapper;
    private final String path;

    private ErrorResponseTest() {
        HashMap<String, String> errors = new HashMap<>();
        errors.put("general", "segmentation fault");

        errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.of(2023, 12, 19, 13, 21, 16))
                .status(418)
                .errors(errors)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        path = "classpath:domain/response/errorResponse.json";
    }

    @Test
    @SneakyThrows
    void shouldSerialize() {
        final String actual = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorResponse);

        final File jsonFile = ResourceUtils.getFile(path);
        final String expected = Files.readString(jsonFile.toPath());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    void shouldDeserialize() {
        final ErrorResponse actual = objectMapper.readValue(
                ResourceUtils.getFile(path), ErrorResponse.class);

        assertThat(actual).isEqualTo(errorResponse);
    }

    @Test
    void shouldCreateErrorResponse() {
        final ErrorResponse response = new ErrorResponse(HttpStatusCode.valueOf(418), "I'm a teapot.");

        assertThat(response.getStatus()).isEqualTo(418);
        assertThat(response.getTimestamp()).isBefore(LocalDateTime.now());
        assertThat(response.getErrors()).hasSize(1);
    }

}
