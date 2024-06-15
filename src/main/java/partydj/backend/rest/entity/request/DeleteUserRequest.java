package partydj.backend.rest.entity.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static partydj.backend.rest.config.UserConfig.PASSWORD_MIN_LENGTH;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteUserRequest {

    @NotNull
    @AssertTrue
    private boolean confirmChoice;

    @NotNull
    @NotBlank
    @Size(min = PASSWORD_MIN_LENGTH, message = "Should be at least {min} characters long.")
    private String password;
}
