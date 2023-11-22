package partydj.backend.rest.mapper;

import org.springframework.stereotype.Component;
import partydj.backend.rest.domain.Artist;
import partydj.backend.rest.domain.response.ArtistResponse;

@Component
public class ArtistMapper {

    public ArtistResponse mapArtistToArtistResponse(final Artist artist) {
        return ArtistResponse.builder()
                .name(artist.getName())
                .build();
    }
}
