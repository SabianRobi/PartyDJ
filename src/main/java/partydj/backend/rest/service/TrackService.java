package partydj.backend.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import partydj.backend.rest.entity.*;
import partydj.backend.rest.repository.PreviousTrackRepository;
import partydj.backend.rest.repository.TrackInQueueRepository;
import partydj.backend.rest.validation.TrackValidator;

import java.util.Set;

@Service
public class TrackService {

    @Autowired
    private TrackInQueueRepository trackInQueueRepository;

    @Autowired
    private PreviousTrackRepository previousTrackRepository;

    @Autowired
    private ArtistService artistService;

    @Autowired
    private TrackValidator validator;

    @Lazy
    @Autowired
    private UserService userService;

    // Repository handlers

    public Track save(final Track track) {
        if (track instanceof TrackInQueue) {
            return trackInQueueRepository.save((TrackInQueue) track);
        } else {
            return previousTrackRepository.save((PreviousTrack) track);
        }
    }

    public void delete(final Track track) {
        // Remove this track from artists
        Set<Artist> artists = track.getArtists();
        artists.forEach(artist -> artist.removeTrack(track));
        artistService.saveAll(artists);

        if (track instanceof PreviousTrack) {
            track.getParty().removePreviousTrack((PreviousTrack) track);
            previousTrackRepository.delete((PreviousTrack) track);
        } else {
            // Remove this track from user's addedTracks
            User user = track.getAddedBy();
            user.removeAddedTrack((TrackInQueue) track);
            userService.save(user);

            trackInQueueRepository.delete((TrackInQueue) track);
        }
    }

    public TrackInQueue findById(final int trackId) {
        final TrackInQueue track = trackInQueueRepository.findById(trackId);

        validator.verifyNotNull(track);

        return track;
    }

    public TrackInQueue getNextTrack(final String partyName) {
        final TrackInQueue track =
                trackInQueueRepository.findTop1ByPartyNameAndIsPlayingIsFalseOrderByScoreDesc(partyName);

        validator.verifyNotNull(track, "There is no track in queue.");

        return track;
    }

    public TrackInQueue getIfExistsNowPlaying(final String partyName) {
        return trackInQueueRepository.findByPartyNameAndIsPlayingIsTrue(partyName);
    }
}
