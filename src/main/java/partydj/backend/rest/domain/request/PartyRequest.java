package partydj.backend.rest.domain.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import partydj.backend.rest.validation.constraint.Name;

import static partydj.backend.rest.config.PartyConfig.PARTY_NAME_MAX_LENGTH;
import static partydj.backend.rest.config.PartyConfig.PARTY_NAME_MIN_LENGTH;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartyRequest {

    @NotNull
    @NotBlank
    @Column(unique = true)
    @Size(min = PARTY_NAME_MIN_LENGTH, message = "Should be at least {min} characters long.")
    @Size(max = PARTY_NAME_MAX_LENGTH, message = "Should be maximum {max} characters long.")
    @Name
    private String name;

    private String password;
}
