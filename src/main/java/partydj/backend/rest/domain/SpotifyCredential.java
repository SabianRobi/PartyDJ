package partydj.backend.rest.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class SpotifyCredential {
    @Id
    @GeneratedValue
    private int id;

    private String state;
    private String token;
    private String refreshToken;

    @OneToOne
    private User owner;
}
