package partydj.backend.rest.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class SpotifyCredential {
    @Id
    @GeneratedValue
    private int id;

    private String state;
    private String token;
    private String refreshToken;

    @OneToOne
    private User owner;

    @Builder
    public SpotifyCredential(int id, String state, String token, String refreshToken, User owner) {
        this.id = id;
        this.state = state;
        this.token = token;
        this.refreshToken = refreshToken;
        this.owner = owner;
    }
}
