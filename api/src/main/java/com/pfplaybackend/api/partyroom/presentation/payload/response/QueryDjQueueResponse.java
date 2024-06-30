package com.pfplaybackend.api.partyroom.presentation.payload.response;

import com.pfplaybackend.api.partyroom.application.dto.DjDto;
import com.pfplaybackend.api.partyroom.application.dto.PlaybackDto;
import com.pfplaybackend.api.partyroom.domain.enums.QueueStatus;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class QueryDjQueueResponse {
    private QueueStatus queueStatus;
    private boolean isRegistered;
    private Map<String, Object> playback;
    private Map<String, Object> dj;
    private List<DjDto> djQueue;
}