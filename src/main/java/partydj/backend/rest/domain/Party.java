package partydj.backend.rest.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Entity
@Data
@NoArgsConstructor
public class Party {
    @Id
    @GeneratedValue
    private int id;

    private String name;
    private String password;
    private String spotifyDeviceId;
    private boolean waitingForTrack;

    @OneToMany
    private Collection<Track> queue;

    @OneToMany
    private Collection<Track> previousTracks;

    @OneToMany
    private Collection<User> users;

    @Builder
    public Party(int id, String name, String password, String spotifyDeviceId, boolean waitingForTrack, Collection<Track> queue, Collection<Track> previousTracks, Collection<User> users) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.spotifyDeviceId = spotifyDeviceId;
        this.waitingForTrack = waitingForTrack;
        this.queue = queue;
        this.previousTracks = previousTracks;
        this.users = users;
    }
}
