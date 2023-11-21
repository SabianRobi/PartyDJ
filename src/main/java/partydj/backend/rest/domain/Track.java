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
    private String title;
    private String artists;
    private String coverUri;
    private int length;
    private int score;

    @Enumerated(EnumType.STRING)
    private PlatformType platformType;

    @ManyToOne
    private User addedBy;
}
