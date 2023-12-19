package partydj.backend.rest.domain.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.validator.constraints.URL;
import partydj.backend.rest.domain.enums.PlatformType;

import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackInQueueResponse {

    @NotNull
    private int id;

    @NotNull
    @NotBlank
    private String title;

    @EqualsAndHashCode.Exclude
    @NotNull
    @NotEmpty
    private Collection<@NotNull ArtistResponse> artists;

    @NotNull
    @NotBlank
    @URL
    private String coverUri;

    @NotNull
    @Positive
    private int length;

    @NotNull
    private PlatformType platformType;

    @NotNull
    private UserInPartyTrackInQueueResponse addedBy;
}
