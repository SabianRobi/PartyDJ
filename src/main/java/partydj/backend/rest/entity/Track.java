package partydj.backend.rest.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.URL;
import partydj.backend.rest.entity.enums.PlatformType;

import java.util.Set;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
public class Track {

    @Id
    @GeneratedValue
    private int id;

    @NotBlank
    private String uri;

    @NotBlank
    private String title;

    @URL
    @NotBlank
    private String coverUri;

    @Positive
    @NotNull
    private int length;

    @EqualsAndHashCode.Exclude
    @NotNull
    @ManyToMany
    private Set<Artist> artists;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PlatformType platformType;

    @EqualsAndHashCode.Exclude
    @NotNull
    @ManyToOne
    private User addedBy;

    @EqualsAndHashCode.Exclude
    @NotNull
    @ManyToOne
    private Party party;
}
