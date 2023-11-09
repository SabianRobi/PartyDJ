package partydj.backend.rest.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    private String name;
    private String password;
    private String spotifyDeviceId;
    private boolean waitingForTrack;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Collection<Track> inQueueTracks;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Collection<Track> previousTracks;

    @OneToMany(fetch = FetchType.EAGER)
    private Collection<User> users;

    public boolean hasPassword() { return password != null; }

    public void addUser(User user) {
        users.add(user);
    }

}
