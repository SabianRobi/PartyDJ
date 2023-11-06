package partydj.backend.rest.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import partydj.backend.rest.domain.enums.PartyRole;
import partydj.backend.rest.domain.enums.UserType;

import java.util.Collection;

@Entity
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Email
    @Column(unique = true)
    private String email;

    @NotBlank
    @Column(unique = true)
    private String username;

    @NotBlank
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(255) default 'NORMAL'")
    private UserType userType;

    @Enumerated(EnumType.STRING)
    private PartyRole partyRole;

    @OneToOne
    private SpotifyCredential spotifyCredential;

    @ManyToOne
    private Party party;

    @OneToMany
    @NotNull
    private Collection<Track> addedTracks;

    @Builder
    public User(int id, String email, String username, String password, UserType userType, PartyRole partyRole, SpotifyCredential spotifyCredential, Party party, Collection<Track> addedTracks) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.userType = userType;
        this.partyRole = partyRole;
        this.spotifyCredential = spotifyCredential;
        this.party = party;
        this.addedTracks = addedTracks;
    }
}
