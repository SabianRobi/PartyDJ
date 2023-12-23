package partydj.backend.rest.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import partydj.backend.rest.validation.constraint.Name;

import java.util.Set;

import static partydj.backend.rest.config.PartyConfig.PARTY_NAME_MAX_LENGTH;
import static partydj.backend.rest.config.PartyConfig.PARTY_NAME_MIN_LENGTH;

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
    @Column(unique = true)
    @Size(min = PARTY_NAME_MIN_LENGTH)
    @Size(max = PARTY_NAME_MAX_LENGTH)
    @Name
    private String name;

    private String password;
    private String spotifyDeviceId;
    private boolean waitingForTrack;

    @EqualsAndHashCode.Exclude
    @NotNull
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<TrackInQueue> tracksInQueue;

    @EqualsAndHashCode.Exclude
    @NotNull
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<PreviousTrack> previousTracks;

    @EqualsAndHashCode.Exclude
    @NotNull
    @NotEmpty
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

    public void removePreviousTrack(final PreviousTrack track) {
        previousTracks.remove(track);
    }

    public void removeUser(final User user) {
        participants.remove(user);
    }
}
