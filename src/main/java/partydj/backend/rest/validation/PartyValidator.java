package partydj.backend.rest.validation;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import partydj.backend.rest.domain.Party;
import partydj.backend.rest.service.PartyService;

@Service
public class PartyValidator {
    @Autowired
    private PartyService partyService;

    public void validateOnPost(Party party) {
        if(party.getName() == null || party.getName().trim().isEmpty()) {
            throw new IllegalStateException("Party name cannot be empty.");
        }

        if (partyService.existsByName(party.getName())) {
            throw new IllegalStateException("A party with this name already exists.");
        }
    }

    public void validateOnGetAndDelete(Party party) {
        if (party == null) {
            throw new EntityNotFoundException("Party does not exists.");
        }
    }
}
