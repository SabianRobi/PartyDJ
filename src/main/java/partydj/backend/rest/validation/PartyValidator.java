package partydj.backend.rest.validation;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import partydj.backend.rest.domain.Party;
import partydj.backend.rest.domain.Track;
import partydj.backend.rest.domain.User;
import partydj.backend.rest.domain.enums.PartyRole;
import partydj.backend.rest.domain.enums.PlatformType;
import partydj.backend.rest.domain.error.RequiredFieldInvalidException;
import partydj.backend.rest.domain.error.RequiredFieldMissingException;
import partydj.backend.rest.domain.request.AddTrackRequest;
import partydj.backend.rest.domain.request.JoinPartyRequest;
import partydj.backend.rest.domain.request.SavePartyRequest;
import partydj.backend.rest.domain.request.SetSpotifyDeviceIdRequest;
import partydj.backend.rest.service.PartyService;

import java.util.List;
import java.util.Objects;

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

        if (!CheckIsCreator(loggedInUser)) {
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

    // Leave
    public void validateOnLeave(final Party party, final User loggedInUser) {
        VerifyPartyNotNull(party);

        if (loggedInUser.getPartyRole() == null) {
            throw new IllegalStateException("You are not in a party.");
        }

        if (!party.getParticipants().contains(loggedInUser)) {
            throw new IllegalStateException("You are not in the given party.");
        }

        if (CheckIsCreator(loggedInUser)) {
            throw new IllegalStateException("You can't leave your own party, only by deleting it.");
        }
    }

    // Search
    public void validateOnSearch(final Party party, final User user,
                                 final String query, final List<PlatformType> platforms,
                                 final int offset, final int limit) {
        // Party
        VerifyPartyNotNull(party);

        // Offset
        if (offset < 0) {
            throw new RequiredFieldInvalidException("Offset cannot be less then zero.");
        }

        // Limit
        if (limit < 1) {
            throw new RequiredFieldInvalidException("Limit cannot be less then one.");
        }

        // Query
        if (query == null || query.length() < 3) {
            throw new RequiredFieldInvalidException("Invalid search query, please be more specific.");
        }

        // User
        VerifyUserIsInParty(user, party);

        // Platforms
        if (platforms == null || platforms.isEmpty() || platforms.contains(null)) {
            throw new RequiredFieldMissingException("Invalid platform selection.");
        }

        if (platforms.contains(PlatformType.SPOTIFY)) {
            VerifyUserIsLoggedInWithSpotify(user);
        }
    }

    // Get tracks in queue
    public void validateOnGetTracks(final Party party, final User user) {
        VerifyPartyNotNull(party);
        VerifyUserIsInParty(user, party);
    }

    // Get previous tracks
    public void validateOnGetPreviousTracks(final Party party, final User user) {
        VerifyPartyNotNull(party);
        VerifyUserIsInParty(user, party);
    }

    // Add track to queue
    public void validateOnAddTrack(final AddTrackRequest addTrackRequest, final Party party, final User user) {
        VerifyPartyNotNull(party);

        // Uri
        if (addTrackRequest.getUri() == null || addTrackRequest.getUri().isBlank()) {
            throw new RequiredFieldMissingException("Track uri cannot be empty.");
        }

        if (addTrackRequest.getPlatformType() == PlatformType.SPOTIFY) {
            String[] splittedUri = addTrackRequest.getUri().split(":");
            if (splittedUri.length != 3 ||
                    !Objects.equals(splittedUri[0], "spotify") ||
                    !Objects.equals(splittedUri[1], "track")) {
                throw new RequiredFieldInvalidException("Invalid track uri.");
            }
        }

        // Platform type
        if (addTrackRequest.getPlatformType() == null) {
            throw new RequiredFieldMissingException("Platform type is invalid.");
        }

        if (addTrackRequest.getPlatformType() == PlatformType.SPOTIFY) {
            VerifyUserIsLoggedInWithSpotify(user);
        }

        // User
        VerifyUserIsInParty(user, party);
    }

    // Set Spotify device id to party
    public void validateOnSetSpotifyDeviceId(final SetSpotifyDeviceIdRequest request,
                                             final Party party, final User user) {
        if (request.getDeviceId() == null || request.getDeviceId().isBlank()) {
            throw new RequiredFieldMissingException("Device id is missing.");
        }

        VerifyPartyNotNull(party);
        VerifyUserIsInParty(user, party);

        if (!CheckIsCreator(user)) {
            throw new AccessDeniedException("You don't have permission to set this id to this party.");
        }

        VerifyUserIsLoggedInWithSpotify(user);
    }

    // Remove track from queue
    public void validateOnRemoveTrackFromQueue(final Track track, final Party party, final User user) {
        VerifyPartyNotNull(party);
        VerifyUserIsInParty(user, party);

        if (track == null) {
            throw new RequiredFieldMissingException("Invalid track.");
        }

        if (!party.getTracksInQueue().contains(track)) {
            throw new IllegalStateException("This track is not in the party's queue.");
        }

        if (track.getAddedBy() != user) {
            throw new AccessDeniedException("You don't have permission to remove other's track from the queue.");
        }
    }

    // Play next track
    public void validateOnPlayNextTrack(final Party party, final User user) {
        VerifyPartyNotNull(party);
        VerifyUserIsInParty(user, party);

        if (!CheckIsCreator(user)) {
            throw new AccessDeniedException("You don't have permission to control playback.");
        }

        if (party.getTracksInQueue().isEmpty()) {
            throw new IllegalStateException("There are no tracks in queue.");
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
            throw new IllegalStateException("You are already in a party.");
        }
    }

    private void VerifyUserIsInParty(final User user, final Party party) {
        if (!party.getParticipants().contains(user)) {
            throw new IllegalStateException("You are not in this party.");
        }
    }

    private void VerifyUserIsLoggedInWithSpotify(final User user) {
        if (user.getSpotifyCredential() == null || user.getSpotifyCredential().getToken() == null) {
            throw new IllegalStateException("You are not logged in with Spotify.");
        }
    }

    private boolean CheckIsCreator(final User user) {
        return user.getPartyRole() == PartyRole.CREATOR;
    }
}
