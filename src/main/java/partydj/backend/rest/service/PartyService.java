package partydj.backend.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import partydj.backend.rest.domain.Party;
import partydj.backend.rest.repository.PartyRepository;

@Service
public class PartyService {
    @Autowired
    private PartyRepository repository;

    public Party save(final Party party) {
        return repository.save(party);
    }

    public void delete(final Party party) {
        repository.delete(party);
    }

    public Party findById(final int partyId) {
        return repository.findById(partyId);
    }

    public boolean existsByName(final String name) {
        return repository.existsByName(name);
    }
}
