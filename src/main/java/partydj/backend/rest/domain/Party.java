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
    private Collection<Track> tracksInQueue;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Collection<Track> previousTracks;

    @OneToMany(fetch = FetchType.EAGER)
    private Collection<User> participants;

    public boolean hasPassword() { return password != null; }

    public void addUser(final User user) {
        participants.add(user);
    }

    public void addTrackToQueue(final Track queueTrack) {
        tracksInQueue.add(queueTrack);
    }

    public void addTrackToPreviousTracks(final Track prevTrack) {
        previousTracks.add(prevTrack);
    }

    public void removeFromPreviousTracks(final Track track) {
        previousTracks.remove(track);
    }

    public void removeTrackFromQueue(final Track track) {
        tracksInQueue.remove(track);
    }

    public void removeUser(final User user) {
        participants.remove(user);
    }
}
