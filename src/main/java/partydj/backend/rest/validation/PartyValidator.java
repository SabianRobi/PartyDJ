package partydj.backend.rest.validation;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import partydj.backend.rest.domain.Party;
import partydj.backend.rest.domain.TrackInQueue;
import partydj.backend.rest.domain.User;
import partydj.backend.rest.domain.enums.PartyRole;
import partydj.backend.rest.domain.enums.PlatformType;
import partydj.backend.rest.domain.error.RequiredFieldInvalidException;
import partydj.backend.rest.domain.request.AddTrackRequest;
import partydj.backend.rest.domain.request.PartyRequest;

import java.util.Objects;
import java.util.Set;

@Component
public class PartyValidator {

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Create
    public void validateOnCreate(final User loggedInUser) {
        verifyUserIsNotInAParty(loggedInUser);
    }

    // Delete
    public void validateOnDelete(final User loggedInUser, final Party party) {
        verifyUserIsInParty(loggedInUser, party);

        if (!checkIsCreator(loggedInUser)) {
            throw new AccessDeniedException("You don't have permission to delete this party.");
        }
    }

    // Load
    public void validateOnLoad(final User loggedInUser, final String partyName) {
        if (loggedInUser.getParty() == null) {
            throw new IllegalStateException("You are not in a party.");
        }
        if (!Objects.equals(loggedInUser.getParty().getName(), partyName)) {
            throw new IllegalStateException("You are not in the given party.");
        }
    }

    // Join
    public void validateOnJoin(final User loggedInUser, final PartyRequest joinRequest, final Party party) {
        verifyUserIsNotInAParty(loggedInUser);

        // Check password matches when set
        if (party.hasPassword() &&
                (joinRequest.getPassword() == null ||
                        !passwordEncoder.matches(joinRequest.getPassword(), party.getPassword()))) {
            throw new RequiredFieldInvalidException("Incorrect password.");
        }
    }

    // Leave
    public void validateOnLeave(final Party party, final User loggedInUser) {
        if (loggedInUser.getParty() == null) {
            throw new IllegalStateException("You are not in a party.");
        }

        if (loggedInUser.getParty() != party) {
            throw new IllegalStateException("You are not in the given party.");
        }

        if (checkIsCreator(loggedInUser)) {
            throw new IllegalStateException("You can't leave your own party, only by deleting it.");
        }
    }

    // Search
    public void validateOnSearch(final User user, final Party party, final Set<PlatformType> platforms) {

        // User
        verifyUserIsInParty(user, party);

        if (platforms.contains(PlatformType.SPOTIFY)) {
            verifyUserIsLoggedInWithSpotify(user);
        }
    }

    // Get tracks in queue
    public void validateOnGetTracks(final Party party, final User user) {
        verifyUserIsInParty(user, party);
    }

    // Add track to queue
    public void validateOnAddTrack(final User loggedInUser, final AddTrackRequest addTrackRequest, final Party party) {
        verifyUserIsInParty(loggedInUser, party);

        if (addTrackRequest.getPlatformType() == PlatformType.SPOTIFY) {
            verifyUserIsLoggedInWithSpotify(loggedInUser);
        }
    }

    // Set Spotify device id to party
    public void validateOnSetSpotifyDeviceId(final User loggedInUser, final Party party) {
        verifyUserIsInParty(loggedInUser, party);
        verifyUserIsLoggedInWithSpotify(loggedInUser);

        if (!checkIsCreator(loggedInUser)) {
            throw new AccessDeniedException("You don't have permission to set this id to this party.");
        }
    }

    // Remove track from queue
    public void validateOnRemoveTrackFromQueue(final TrackInQueue track, final Party party, final User user) {
        verifyUserIsInParty(user, party);

        if (!party.getTracksInQueue().contains(track)) {
            throw new IllegalStateException("This track is not in the party's queue.");
        }

        if (track.isPlaying()) {
            throw new IllegalStateException("Can't delete track from queue, it is now playing.");
        }

        if (track.getAddedBy() != user) {
            throw new AccessDeniedException("You don't have permission to remove other's track from the queue.");
        }
    }

    // Play next track
    public void validateOnPlayNextTrack(final Party party, final User user) {
        verifyUserIsInParty(user, party);

        if (!checkIsCreator(user)) {
            throw new AccessDeniedException("You don't have permission to control playback.");
        }

        if (party.getSpotifyDeviceId() == null) {
            throw new IllegalStateException("Party's playback device is not set.");
        }
    }

    // Helper verifiers

    public void verifyNotNull(final Party party) {
        if (party == null) {
            throw new EntityNotFoundException("Party does not exists.");
        }
    }

    private void verifyUserIsNotInAParty(final User user) {
        if (user.getParty() != null) {
            throw new IllegalStateException("You are already in a party.");
        }
    }

    private void verifyUserIsInParty(final User user, final Party party) {
        if (user.getParty() != party) {
            throw new IllegalStateException("You are not in this party.");
        }
    }

    private void verifyUserIsLoggedInWithSpotify(final User user) {
        if (user.getSpotifyCredential() == null || user.getSpotifyCredential().getToken() == null) {
            throw new IllegalStateException("You are not logged in with Spotify.");
        }
    }

    private boolean checkIsCreator(final User user) {
        return user.getPartyRole() == PartyRole.CREATOR;
    }
}
