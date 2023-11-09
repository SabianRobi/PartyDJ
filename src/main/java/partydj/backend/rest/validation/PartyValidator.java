package partydj.backend.rest.validation;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import partydj.backend.rest.domain.Party;
import partydj.backend.rest.domain.User;
import partydj.backend.rest.domain.request.JoinPartyRequest;
import partydj.backend.rest.service.PartyService;

import java.util.Objects;

@Service
public class PartyValidator {
    @Autowired
    private PartyService partyService;

    // Create
    public void validateOnPost(final Party party) {
        if (party.getName() == null || party.getName().isBlank()) {
            throw new IllegalStateException("Party name cannot be empty.");
        }

        if (partyService.existsByName(party.getName())) {
            throw new IllegalStateException("A party with this name already exists.");
        }
    }

    // Get & Delete
    public void validateOnGetAndDelete(final Party party) {
        checkPartyExists(party);
    }

    // Join
    public void validateOnJoin(final JoinPartyRequest joinRequest, final Party party, final User user) {
        // Check user is already at a party
        if (user.getPartyRole() != null) {
            throw new IllegalStateException("User is already in a party.");
        }

        checkPartyExists(party);

        // Check password matches when required
        if (party.hasPassword() &&
                (joinRequest.getPassword() == null ||
                        !Objects.equals(party.getPassword(), joinRequest.getPassword().trim()))) {
            throw new IllegalStateException("Incorrect password.");
        }
    }

    private void checkPartyExists(final Party party) {
        if (party == null) {
            throw new EntityNotFoundException("Party does not exists.");
        }
    }
}
