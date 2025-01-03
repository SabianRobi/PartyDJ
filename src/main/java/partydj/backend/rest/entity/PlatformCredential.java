package partydj.backend.rest.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
public abstract class PlatformCredential {
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
