package partydj.backend.rest.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpotifyCredential {

    @Id
    @GeneratedValue
    private int id;

    @UUID
    private String state;

    private String token;
    private String refreshToken;

    @EqualsAndHashCode.Exclude
    @NotNull
    @OneToOne
    private User owner;
}
