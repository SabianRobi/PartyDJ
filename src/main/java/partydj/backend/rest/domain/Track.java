package partydj.backend.rest.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import partydj.backend.rest.domain.enums.PlatformType;

@Entity
@Data
@NoArgsConstructor
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

    @ManyToOne
    private Party party;

    @Builder
    public Track(int id, String uri, int score, PlatformType platform, User addedBy, Party party) {
        this.id = id;
        this.uri = uri;
        this.score = score;
        this.platform = platform;
        this.addedBy = addedBy;
        this.party = party;
    }
}
