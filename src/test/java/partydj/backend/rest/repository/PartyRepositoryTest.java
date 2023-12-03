package partydj.backend.rest.repository;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import partydj.backend.rest.domain.Party;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PartyRepositoryTest {
    @Autowired
    private PartyRepository partyRepository;

    @Autowired
    TestEntityManager entityManager;

    private Party party;

    @BeforeEach
    void init() {
        party = Party.builder().name("party").build();
    }

    @Test
    public void givenNewParty_whenSave_thenSuccess() {
        Party savedParty = partyRepository.save(party);

        assertThat(entityManager.find(Party.class, savedParty.getId())).isEqualTo(party);
    }

    @Test
    public void givenParty_whenDelete_thenSuccess() {
        entityManager.persist(party);

        partyRepository.delete(party);

        assertThat(entityManager.find(Party.class, party.getId())).isNull();
    }

}