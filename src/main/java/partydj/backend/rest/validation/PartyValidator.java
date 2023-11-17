package partydj.backend.rest.validation;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import partydj.backend.rest.domain.Party;
import partydj.backend.rest.domain.User;
import partydj.backend.rest.domain.enums.PartyRole;
import partydj.backend.rest.domain.error.RequiredFieldInvalidException;
import partydj.backend.rest.domain.error.RequiredFieldMissingException;
import partydj.backend.rest.domain.request.JoinPartyRequest;
import partydj.backend.rest.domain.request.SavePartyRequest;
import partydj.backend.rest.service.PartyService;

import java.util.Objects;

import static partydj.backend.rest.config.PartyConfig.PARTY_NAME_MIN_LENGTH;
import static partydj.backend.rest.config.UserConfig.USERNAME_MIN_LENGTH;

@Service
public class PartyValidator {
    @Autowired
    private PartyService partyService;

    // Create
    public void validateOnPost(final SavePartyRequest party, final User loggedInUser) {
        VerifyUserIsNotInAParty(loggedInUser);

        if (party.getName() == null || party.getName().isBlank()) {
            throw new RequiredFieldMissingException("Party name cannot be empty.");
        }
        VerifyPartyNameMinLength(party.getName());

        if (partyService.existsByName(party.getName())) {
            throw new IllegalStateException("A party with this name already exists.");
        }
    }

    // Delete
    public void validateOnDelete(final Party party, final User loggedInUser) {
        VerifyPartyNotNull(party);

        if(loggedInUser.getPartyRole() != PartyRole.CREATOR || !party.getUsers().contains(loggedInUser)) {
            throw new AccessDeniedException("You don't have permission to delete this party.");
        }
    }

    // Join
    public void validateOnJoin(final JoinPartyRequest joinRequest, final User loggedInUser) {

        if (joinRequest.getName() == null || joinRequest.getName().isBlank()) {
            throw new RequiredFieldMissingException("Party name cannot be empty.");
        }

        VerifyUserIsNotInAParty(loggedInUser);

        Party party = partyService.findByName(joinRequest.getName());
        VerifyPartyNotNull(party);

        // Check password matches when required
        if (party.hasPassword() &&
                (joinRequest.getPassword() == null ||
                        !Objects.equals(party.getPassword(), joinRequest.getPassword().trim()))) {
            throw new IllegalStateException("Incorrect password.");
        }
    }

    private void VerifyPartyNotNull(final Party party) {
        if (party == null) {
            throw new EntityNotFoundException("Party does not exists.");
        }
    }

    private void VerifyPartyNameMinLength(final String partyName) {
        if (partyName.trim().length() < USERNAME_MIN_LENGTH) {
            throw new RequiredFieldInvalidException("Party name must be at least " + PARTY_NAME_MIN_LENGTH + " characters long.");
        }
    }

    private void VerifyUserIsNotInAParty(final User user) {
        if (user.getPartyRole() != null) {
            throw new IllegalStateException("User is already in a party.");
        }
    }
}
