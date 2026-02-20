package com.pfplaybackend.api.party.domain.event;

import com.pfplaybackend.api.common.domain.event.DomainEvent;
import com.pfplaybackend.api.party.application.dto.playback.PlaybackDto;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import lombok.Getter;

@Getter
public class PlaybackStartedEvent extends DomainEvent {
    private final PartyroomId partyroomId;
    private final long crewId;
    private final PlaybackDto playback;

    public PlaybackStartedEvent(PartyroomId partyroomId, long crewId, PlaybackDto playback) {
        this.partyroomId = partyroomId;
        this.crewId = crewId;
        this.playback = playback;
    }
}
