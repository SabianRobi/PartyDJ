package partydj.backend.rest.repository;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import partydj.backend.rest.domain.Party;
import partydj.backend.rest.domain.User;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static partydj.backend.rest.helper.DataGenerator.generateParty;
import static partydj.backend.rest.helper.DataGenerator.generateUser;

@DataJpaTest
public class PartyRepositoryTest {
    @Autowired
    private PartyRepository partyRepository;

    @Autowired
    private TestEntityManager entityManager;

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

    @Test
    public void givenParty_whenFindByName_thenSuccess() {
        final User user = entityManager.persist(generateUser(""));
        final Party party = entityManager.persist(generateParty("", Set.of(user)));
        entityManager.persist(party);

        final Party foundParty = partyRepository.findByName(party.getName());

        assertThat(foundParty).isNotNull();
    }

}