package partydj.backend.rest.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import partydj.backend.rest.entity.enums.PartyRole;
import partydj.backend.rest.entity.enums.UserType;
import partydj.backend.rest.validation.constraint.Name;

import java.util.Set;

import static partydj.backend.rest.config.UserConfig.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotNull
    @Column(unique = true)
    @Email(regexp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")
    private String email;

    @NotBlank
    @Column(unique = true)
    @Size(min = USERNAME_MIN_LENGTH, message = "Should be at least {min} characters long.")
    @Size(max = USERNAME_MAX_LENGTH, message = "Should be maximum {max} characters long.")
    @Name
    private String username;

    @NotBlank
    @Size(min = PASSWORD_MIN_LENGTH, message = "Should be at least {min} characters long.")
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(255) default 'NORMAL'")
    private UserType userType;

    @Enumerated(EnumType.STRING)
    private PartyRole partyRole;

    @EqualsAndHashCode.Exclude
    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    private SpotifyCredential spotifyCredential;

    @EqualsAndHashCode.Exclude
    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    private GoogleCredential googleCredential;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    private Party party;

    @EqualsAndHashCode.Exclude
    @NotNull
    @OneToMany
    private Set<TrackInQueue> addedTracks;

    public void addAddedTrack(final TrackInQueue track) {
        addedTracks.add(track);
    }

    public void removeAddedTrack(final TrackInQueue track) {
        addedTracks.remove(track);
    }
}
