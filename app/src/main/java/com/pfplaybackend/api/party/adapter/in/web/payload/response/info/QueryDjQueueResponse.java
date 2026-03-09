package com.pfplaybackend.api.party.adapter.in.web.payload.response.info;

import com.pfplaybackend.api.party.application.dto.dj.DjWithProfileDto;
import com.pfplaybackend.api.party.domain.entity.data.PlaybackData;
import com.pfplaybackend.api.party.domain.enums.QueueStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
public class QueryDjQueueResponse {
    private boolean playbackActivated;
    private QueueStatus queueStatus;
    private boolean registered;
    private PlaybackSummary playback;
    private List<DjWithProfileDto> djs;

    public static QueryDjQueueResponse from(boolean playbackActivated, QueueStatus queueStatus,
                                            boolean registered, PlaybackData playback, List<DjWithProfileDto> djs) {

        PlaybackSummary summary = Objects.nonNull(playback) ? PlaybackSummary.from(playback) : null;
        return new QueryDjQueueResponse(
                playbackActivated,
                queueStatus,
                registered,
                summary,
                djs
        );
    }
}
