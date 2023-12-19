package partydj.backend.rest.domain.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.URL;
import partydj.backend.rest.domain.enums.PlatformType;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreviousTrackResponse {

    @NotNull
    @NotBlank
    private String title;

    @NotNull
    @NotBlank
    @URL
    private String coverUri;

    @NotNull
    @Positive
    private int length;

    @EqualsAndHashCode.Exclude
    @NotNull
    @NotEmpty
    private Collection<@NotNull ArtistResponse> artists;

    @NotNull
    private PlatformType platformType;

    @NotNull
    private UserInPartyTrackInQueueResponse addedBy;

    @NotNull
    @PastOrPresent
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endedAt;
}
