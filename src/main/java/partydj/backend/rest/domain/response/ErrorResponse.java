package partydj.backend.rest.domain.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;
import java.util.HashMap;

@Builder
@AllArgsConstructor
public class ErrorResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime timestamp;

    @JsonProperty
    private int status;

    @JsonProperty
    private HashMap<String, String> errors;

    public ErrorResponse(final HttpStatusCode status, final String error) {
        errors = new HashMap<>();
        errors.put("general", error);
        this.status = status.value();
        this.timestamp = LocalDateTime.now();
    }
}
