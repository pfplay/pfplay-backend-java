package com.pfplaybackend.api.party.domain.event;

import com.pfplaybackend.api.common.domain.event.DomainEvent;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.value.PlaybackSnapshot;
import lombok.Getter;

@Getter
public class PlaybackStartedEvent extends DomainEvent {
    private final PartyroomId partyroomId;
    private final long crewId;
    private final PlaybackSnapshot playback;

    public PlaybackStartedEvent(PartyroomId partyroomId, long crewId, PlaybackSnapshot playback) {
        this.partyroomId = partyroomId;
        this.crewId = crewId;
        this.playback = playback;
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(partyroomId.getId());
    }
}
