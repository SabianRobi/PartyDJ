package partydj.backend.rest.domain.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import partydj.backend.rest.validation.constraint.Name;

import static partydj.backend.rest.config.UserConfig.*;

@Builder
@Getter
@Setter
public class UserRequest {

    @NotNull
    @NotBlank
    @Column(unique = true)
    @Email(regexp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")
    private String email;

    @NotNull
    @NotBlank
    @Column(unique = true)
    @Size(min = USERNAME_MIN_LENGTH, message = "Should be at least {min} characters long.")
    @Size(max = USERNAME_MAX_LENGTH, message = "Should be maximum {max} characters long.")
    @Name
    private String username;

    @NotNull
    @NotBlank
    @Size(min = PASSWORD_MIN_LENGTH, message = "Should be at least " + PASSWORD_MIN_LENGTH + " characters long.")
    private String password;
}
