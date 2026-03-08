package com.pfplaybackend.api.party.adapter.in.web.payload.response.info;

import com.pfplaybackend.api.party.application.dto.dj.DjWithProfileDto;
import com.pfplaybackend.api.party.domain.entity.data.PlaybackData;
import com.pfplaybackend.api.party.domain.enums.QueueStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
@AllArgsConstructor
public class QueryDjQueueResponse {
    private boolean playbackActivated;
    private QueueStatus queueStatus;
    private boolean registered;
    private Map<String, Object> playback;
    private List<DjWithProfileDto> djs;

    public static QueryDjQueueResponse from(boolean playbackActivated, QueueStatus queueStatus,
                                            boolean registered, PlaybackData playback, List<DjWithProfileDto> djs) {

        Map<String, Object> map = null;
        if(Objects.nonNull(playback)) {
            map = new HashMap<>();
            map.put("name", playback.getName());
            map.put("thumbnailImage", playback.getThumbnailImage());
            map.put("duration", playback.getDuration().toDisplayString());
        }
        return new QueryDjQueueResponse(
                playbackActivated,
                queueStatus,
                registered,
                map,
                djs
        );
    }
}