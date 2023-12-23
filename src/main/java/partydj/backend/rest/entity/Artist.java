package partydj.backend.rest.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Artist {

    @Id
    @GeneratedValue
    private int id;

    @NotBlank
    @Column(unique = true)
    private String name;

    @EqualsAndHashCode.Exclude
    @NotNull
    @ManyToMany
    private Set<Track> tracks;

    public void addTrack(final Track track) {
        tracks.add(track);
    }

    public void removeTrack(final Track track) {
        tracks.remove(track);
    }
}
