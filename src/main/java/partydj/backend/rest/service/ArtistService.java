package partydj.backend.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import partydj.backend.rest.domain.Artist;
import partydj.backend.rest.repository.ArtistsRepository;

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
}
