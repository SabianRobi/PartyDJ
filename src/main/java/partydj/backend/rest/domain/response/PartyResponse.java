package partydj.backend.rest.domain.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import partydj.backend.rest.validation.constraint.Name;

import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartyResponse {

    @NotNull
    @Positive
    private int id;

    @NotNull
    @NotBlank
    @Name
    private String name;

    @EqualsAndHashCode.Exclude
    @NotNull
    private Collection<@NotNull TrackInQueueResponse> tracksInQueue;

//    @NotNull
//    private Collection<TrackInQueueResponse> previousTracks;

    @EqualsAndHashCode.Exclude
    @NotNull
    @NotEmpty
    private Collection<@NotNull UserInPartyResponse> participants;
}
