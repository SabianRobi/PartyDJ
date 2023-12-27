package partydj.backend.rest.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import partydj.backend.rest.entity.Artist;
import partydj.backend.rest.entity.Party;
import partydj.backend.rest.entity.TrackInQueue;
import partydj.backend.rest.entity.User;
import partydj.backend.rest.helper.DataGenerator;
import partydj.backend.rest.repository.ArtistRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ArtistServiceTest {

    @Mock
    private ArtistRepository repository;

    @InjectMocks
    private ArtistService artistService;

    @Test
    void shouldRegister() {
        final Artist artist = DataGenerator.generateArtist();
        when(repository.save(any())).thenReturn(artist);

        final Artist registeredArtist = artistService.register(artist);

        assertThat(registeredArtist).isSameAs(artist);
    }

    @Test
    void shouldSave() {
        final Artist artist = DataGenerator.generateArtist();
        when(repository.save(any())).thenReturn(artist);

        final Artist registeredArtist = artistService.save(artist);

        assertThat(registeredArtist).isSameAs(artist);
    }

    @Test
    void givenArtistsWithTrack_whenFindAllByTracksContainingTrack_thenSuccess() {
        final User user = DataGenerator.generateUser();
        final Party party = DataGenerator.generateParty("", Set.of(user));
        final Artist artist1 = DataGenerator.generateArtist("1");
        final Artist artist2 = DataGenerator.generateArtist("2");
        final HashSet<Artist> artists = new HashSet<>(Set.of(artist1, artist2));
        final TrackInQueue track = DataGenerator.generateTrackInQueue("1", party, user, artists);
        when(repository.findAllByTracksContaining(any())).thenReturn(artists);

        final HashSet<Artist> foundArtists = artistService.findAllByTracksContaining(track);

        assertThat(foundArtists).isSameAs(artists);
    }

    @Test
    void givenArtists_whenFindAllByNameInList_thenSuccess() {
        final Artist artist1 = DataGenerator.generateArtist("1");
        final Artist artist2 = DataGenerator.generateArtist("2");
        final HashSet<Artist> artists = new HashSet<>(Set.of(artist1, artist2));
        when(repository.findAllByNameIn(any())).thenReturn(artists);

        final HashSet<Artist> foundArtists = artistService.findAllByNameIn(List.of("1artist", "2artist"));

        assertThat(foundArtists).isSameAs(artists);
    }

    @Test
    void givenArtists_whenSaveAll_thenSuccess() {
        final Artist artist1 = DataGenerator.generateArtist("1");
        final Artist artist2 = DataGenerator.generateArtist("2");
        final HashSet<Artist> artists = new HashSet<>(Set.of(artist1, artist2));
        when(repository.saveAll(any())).thenReturn(artists);

        final HashSet<Artist> foundArtists = artistService.saveAll(artists);

        assertThat(foundArtists).isSameAs(artists);
    }
}
