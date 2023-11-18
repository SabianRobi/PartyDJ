package partydj.backend.rest.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import partydj.backend.rest.domain.Track;
import partydj.backend.rest.domain.response.TrackInQueueResponse;

@Component
public class TrackMapper {

    @Autowired
    UserMapper userMapper;

    public TrackInQueueResponse mapTrackToTrackInQueue(final Track track) {
        return TrackInQueueResponse.builder()
                .id(track.getId())
                .uri(track.getUri())
                .platformType(track.getPlatform())
                .addedBy(userMapper.mapUserToUserInPartyTrackInQueueResponse(track.getAddedBy()))
                .build();
    }
}
