package partydj.backend.rest.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Set;

@Data
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

    @EqualsAndHashCode.Exclude
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<TrackInQueue> tracksInQueue;

    @EqualsAndHashCode.Exclude
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<PreviousTrack> previousTracks;

    @EqualsAndHashCode.Exclude
    @OneToMany(fetch = FetchType.EAGER)
    private Set<User> participants;

    public boolean hasPassword() {
        return password != null;
    }

    public void addUser(final User user) {
        participants.add(user);
    }

    public void addTrackToQueue(final TrackInQueue track) {
        tracksInQueue.add(track);
    }

    public void addTrackToPreviousTracks(final PreviousTrack track) {
        previousTracks.add(track);
    }

    public void removeTrackFromQueue(final TrackInQueue track) {
        tracksInQueue.remove(track);
    }

    public void removeUser(final User user) {
        participants.remove(user);
    }
}
