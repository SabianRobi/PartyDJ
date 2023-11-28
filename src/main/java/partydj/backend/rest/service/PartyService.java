package partydj.backend.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import partydj.backend.rest.domain.Party;
import partydj.backend.rest.domain.Track;
import partydj.backend.rest.domain.User;
import partydj.backend.rest.repository.PartyRepository;

import java.util.HashSet;
import java.util.Set;

@Service
public class PartyService {
    @Autowired
    private PartyRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Lazy
    @Autowired
    private UserService userService;

    @Autowired
    private TrackService trackService;

    public Party register(final Party party) {
        party.setName(party.getName().trim());
        party.setPassword(passwordEncoder.encode(party.getPassword()));
        return repository.save(party);
    }

    public Party save(final Party party) {
        return repository.save(party);
    }

    public void delete(final Party party) {
        // Update users
        Set<User> users = party.getParticipants();
        users.forEach(user -> {
            user.setParty(null);
            user.setPartyRole(null);
        });
        userService.saveAll(users);

        // Deletes tracks
        HashSet<Track> tracks = new HashSet<>(party.getTracksInQueue());
        tracks.addAll(party.getPreviousTracks());

        tracks.forEach(track -> trackService.delete(track));

        repository.delete(party);
    }

    public boolean existsByName(final String name) {
        return repository.existsByName(name);
    }

    public Party findByName(final String name) {
        return repository.findByName(name);
    }
}
