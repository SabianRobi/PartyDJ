package partydj.backend.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import partydj.backend.rest.domain.Artist;
import partydj.backend.rest.domain.Track;
import partydj.backend.rest.repository.ArtistsRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ArtistService {

    @Autowired
    private ArtistsRepository repository;

    public Artist register(final Artist artist) {
        return repository.save(artist);
    }

    public Artist save(final Artist artist) {
        return repository.save(artist);
    }

    public HashSet<Artist> findAllByTracksContaining(final Track track) {
        return repository.findAllByTracksContaining(track);
    }

    public HashSet<Artist> findAllByNameIn(final List<String> artistNames) {
        return repository.findAllByNameIn(artistNames);
    }

    public void saveAll(final Set<Artist> artists) {
        repository.saveAll(artists);
    }
}
