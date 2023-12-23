package partydj.backend.rest.entity.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;
import java.util.HashMap;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    @NotNull
    @Positive
    @JsonProperty
    private int status;

    @NotNull
    @NotEmpty
    @JsonProperty
    private HashMap<@NotNull @NotBlank String, @NotNull @NotBlank String> errors;

    public ErrorResponse(final HttpStatusCode status, final String error) {
        errors = new HashMap<>();
        errors.put("general", error);
        this.status = status.value();
        this.timestamp = LocalDateTime.now();
    }
}
