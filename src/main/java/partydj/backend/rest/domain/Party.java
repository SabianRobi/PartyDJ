package partydj.backend.rest.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Party {
    @Id
    @GeneratedValue
    private int id;

    private String name;
    private String password;
    private String spotifyDeviceId;
    private boolean waitingForTrack;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Collection<Track> inQueueTracks;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Collection<Track> previousTracks;

    @OneToMany
    private Collection<User> users;
}
