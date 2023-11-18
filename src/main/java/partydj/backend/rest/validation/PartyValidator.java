package partydj.backend.rest.validation;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import partydj.backend.rest.domain.Party;
import partydj.backend.rest.domain.User;
import partydj.backend.rest.domain.enums.PartyRole;
import partydj.backend.rest.domain.error.RequiredFieldInvalidException;
import partydj.backend.rest.domain.error.RequiredFieldMissingException;
import partydj.backend.rest.domain.request.JoinPartyRequest;
import partydj.backend.rest.domain.request.SavePartyRequest;
import partydj.backend.rest.service.PartyService;

import static partydj.backend.rest.config.PartyConfig.PARTY_NAME_MIN_LENGTH;
import static partydj.backend.rest.config.UserConfig.USERNAME_MIN_LENGTH;

@Service
public class PartyValidator {
    @Autowired
    private PartyService partyService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Create
    public void validateOnPost(final SavePartyRequest party, final User loggedInUser) {
        VerifyUserIsNotInAParty(loggedInUser);

        if (party.getName() == null || party.getName().isBlank()) {
            throw new RequiredFieldMissingException("Party name cannot be empty.");
        }

        if (party.getName().trim().length() < USERNAME_MIN_LENGTH) {
            throw new RequiredFieldInvalidException("Party name must be at least " + PARTY_NAME_MIN_LENGTH + " characters long.");
        }

        if (partyService.existsByName(party.getName())) {
            throw new IllegalStateException("A party with this name already exists.");
        }
    }

    // Get
    public void validateOnGet(final Party party, final User loggedInUser) {
        VerifyPartyNotNull(party);
        VerifyUserIsInParty(loggedInUser, party);
    }

    // Delete
    public void validateOnDelete(final Party party, final User loggedInUser) {
        VerifyPartyNotNull(party);
        VerifyUserIsInParty(loggedInUser, party);

        if (!CheckHasRole(loggedInUser, PartyRole.CREATOR)) {
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
                        !passwordEncoder.matches(joinRequest.getPassword(), party.getPassword()))) {
            throw new RequiredFieldInvalidException("Incorrect password.");
        }
    }

    public void validateOnLeave(final Party party, final User loggedInUser) {
        VerifyPartyNotNull(party);

        if (loggedInUser.getPartyRole() == null) {
            throw new IllegalStateException("User is not in a party.");
        }

        if (!party.getParticipants().contains(loggedInUser)) {
            throw new IllegalStateException("User is not participating the given party.");
        }

        if(CheckHasRole(loggedInUser, PartyRole.CREATOR)) {
            throw new IllegalStateException("You can't leave your own party, only by deleting it.");
        }
    }

    // Helper verifiers
    private void VerifyPartyNotNull(final Party party) {
        if (party == null) {
            throw new EntityNotFoundException("Party does not exists.");
        }
    }

    private void VerifyUserIsNotInAParty(final User user) {
        if (user.getPartyRole() != null) {
            throw new IllegalStateException("User is already in a party.");
        }
    }

    private void VerifyUserIsInParty(final User user, final Party party) {
        if (!party.getParticipants().contains(user)) {
            throw new IllegalStateException("You are not in this party.");
        }
    }

    private boolean CheckHasRole(final User user, final PartyRole partyRole) {
        return user.getPartyRole() == partyRole;
    }
}
