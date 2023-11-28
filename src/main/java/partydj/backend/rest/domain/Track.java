package partydj.backend.rest.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import partydj.backend.rest.domain.enums.PlatformType;

import java.util.Set;

@Getter
@Setter
@Entity
@SuperBuilder
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

    @ManyToMany
    private Set<Artist> artists;

    @Enumerated(EnumType.STRING)
    private PlatformType platformType;

    @ManyToOne
    private User addedBy;

    @ManyToOne
    private Party party;
}
