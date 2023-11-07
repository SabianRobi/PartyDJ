package partydj.backend.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import partydj.backend.rest.domain.SpotifyCredential;
import partydj.backend.rest.repository.SpotifyCredentialRepository;

@Service
public class SpotifyCredentialService {
    @Autowired
    private SpotifyCredentialRepository repository;

    public SpotifyCredential save(final SpotifyCredential spotifyCredential) {
        return repository.save(spotifyCredential);
    }

    public void delete(final SpotifyCredential spotifyCredential) {
        repository.delete(spotifyCredential);
    }

    public SpotifyCredential findById(final int spotifyCredentialId) {
        return repository.findById(spotifyCredentialId);
    }
}
