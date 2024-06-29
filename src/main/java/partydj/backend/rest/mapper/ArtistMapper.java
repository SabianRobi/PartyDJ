package partydj.backend.rest.mapper;

import org.springframework.stereotype.Component;
import partydj.backend.rest.entity.Artist;
import partydj.backend.rest.entity.response.ArtistResponse;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;

@Component
public class ArtistMapper {

    public ArtistResponse mapArtistToArtistResponse(final Artist artist) {
        return ArtistResponse.builder()
                .name(artist.getName())
                .build();
    }

    public ArtistResponse mapSimplifiedArtistToArtistResponse(final ArtistSimplified artistSimplified) {
        return ArtistResponse.builder()
                .name(artistSimplified.getName())
                .build();
    }
}
