package partydj.backend.rest.domain;

import jakarta.persistence.*;
import lombok.Data;
import partydj.backend.rest.domain.enums.PartyRole;
import partydj.backend.rest.domain.enums.UserType;

import java.util.Collection;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String email;
    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Enumerated(EnumType.STRING)
    private PartyRole partyRole;

    @OneToOne
    private SpotifyCredential spotifyCredential;

    @ManyToOne
    private Party party;

    @OneToMany
    private Collection<Track> addedTracks;
}
