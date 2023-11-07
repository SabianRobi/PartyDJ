package partydj.backend.rest.domain;

import jakarta.persistence.*;
import lombok.*;
import partydj.backend.rest.domain.enums.PlatformType;

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

    private String uri;
    private int score;

    @Enumerated(EnumType.STRING)
    private PlatformType platform;

    @ManyToOne
    private User addedBy;
}
