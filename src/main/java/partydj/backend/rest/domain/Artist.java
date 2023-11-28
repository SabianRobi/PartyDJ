package partydj.backend.rest.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Artist {
    @Id
    @GeneratedValue
    private int id;

    @NotBlank
    private String name;

    @ManyToMany
    private Set<Track> tracks;

    public void addTrack(final Track track) {
        tracks.add(track);
    }

    public void removeTrack(final Track track) {
        tracks.remove(track);
    }
}
