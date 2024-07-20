package com.pfplaybackend.api.partyroom.presentation.payload.response;

import com.pfplaybackend.api.partyroom.application.dto.DjDto;
import com.pfplaybackend.api.partyroom.application.dto.DjWithProfileDto;
import com.pfplaybackend.api.partyroom.application.dto.PlaybackDto;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Playback;
import com.pfplaybackend.api.partyroom.domain.enums.QueueStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
@AllArgsConstructor
public class QueryDjQueueResponse {
    private boolean isPlaybackActivated;
    private QueueStatus queueStatus;
    private boolean isRegistered;
    private Map<String, Object> playback;
    private List<DjWithProfileDto> djs;

    public static QueryDjQueueResponse from(boolean isPlaybackActivated, QueueStatus queueStatus,
                                            boolean isRegistered, Playback playback, List<DjWithProfileDto> djs) {

        Map<String, Object> map = null;
        if(Objects.nonNull(playback)) {
            map = new HashMap<>();
            map.put("name", playback.getName());
            map.put("thumbnailImage", playback.getThumbnailImage());
        }
        return new QueryDjQueueResponse(
                isPlaybackActivated,
                queueStatus,
                isRegistered,
                map,
                djs
        );
    }
}