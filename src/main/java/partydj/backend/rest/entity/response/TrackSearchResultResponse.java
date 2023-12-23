package partydj.backend.rest.entity.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.validator.constraints.URL;
import partydj.backend.rest.entity.enums.PlatformType;
import partydj.backend.rest.validation.constraint.TrackUri;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackSearchResultResponse {

    @NotNull
    @NotBlank
    @TrackUri
    private String uri;

    @NotNull
    @NotBlank
    private String title;

    @EqualsAndHashCode.Exclude
    @NotNull
    @NotEmpty
    private Set<@NotNull @NotBlank String> artists;

    @NotNull
    @NotBlank
    @URL
    private String coverUri;

    @NotNull
    @Positive
    private int length;

    @NotNull
    private PlatformType platformType;
}
