package partydj.backend.rest.domain;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import partydj.backend.rest.domain.enums.PlatformType;

import java.util.Collection;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Track {
    @Id
    @GeneratedValue
    private int id;
    @NotBlank
    private String uri;
    @NotBlank
    private String title;
    @NotBlank
    private String coverUri;
    @Min(0)
    private int length;
    private int score;

    @ManyToMany(cascade = CascadeType.REMOVE)
    private Collection<Artist> artists;

    @Enumerated(EnumType.STRING)
    private PlatformType platformType;

    @ManyToOne
    private User addedBy;

    @ManyToOne
    private Party party;
}
