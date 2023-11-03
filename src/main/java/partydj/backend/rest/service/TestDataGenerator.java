package partydj.backend.rest.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import partydj.backend.rest.domain.Party;
import partydj.backend.rest.domain.SpotifyCredential;
import partydj.backend.rest.domain.Track;
import partydj.backend.rest.domain.User;
import partydj.backend.rest.domain.enums.PlatformType;
import partydj.backend.rest.domain.enums.UserType;
import partydj.backend.rest.repository.PartyRepository;
import partydj.backend.rest.repository.SpotifyCredentialRepository;
import partydj.backend.rest.repository.TrackRepository;
import partydj.backend.rest.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Component
public class TestDataGenerator {
    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PartyRepository partyRepository;

    @Autowired
    private SpotifyCredentialRepository spotifyCredentialRepository;

    @Transactional
    public void createTestData() {
        User user = createUser(UserType.ADMIN, "admin@partydj.com", "admin", "password");
        userRepository.save(user);
        SpotifyCredential spotifyCredential = createSpotifyCredential(user);
        spotifyCredentialRepository.save(spotifyCredential);
        Party party = createParty("Test party", List.of(user));
        partyRepository.save(party);
        Track track = createTrack(PlatformType.SPOTIFY, party, user);
        user.setAddedTracks(new ArrayList<Track>());
        userRepository.save(user);
        trackRepository.save(track);
    }

    private SpotifyCredential createSpotifyCredential(User owner) {
        SpotifyCredential spotifyCredential = SpotifyCredential.builder()
                .owner(owner)
                .refreshToken("refreshTokenComesHere")
                .token("realTokenComesHere")
                .build();
        return spotifyCredential;
    }

    private User createUser(UserType userType, String email, String username, String password) {
        User user = User.builder()
                .userType(userType)
                .email(email)
                .username(username)
                .password(password)
                .build();
        return user;
    }

    private Party createParty(String name, List<User> users) {
        Party party = Party.builder()
                .name(name)
                .waitingForTrack(true)
                .users(users)
                .build();
        return party;
    }

    private Track createTrack(PlatformType platformType, Party party, User addedBy) {
        Track track = Track.builder()
                .uri("random_uri_comes_here")
                .score(0)
                .platform(platformType)
                .addedBy(addedBy)
                .party(party)
                .build();
        return track;
    }
}
