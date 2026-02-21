package com.pfplaybackend.api.party.domain.event;

import com.pfplaybackend.api.common.domain.event.DomainEvent;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.value.PlaybackId;
import lombok.Getter;

@Getter
public class PlaybackDeactivatedEvent extends DomainEvent {
    private final PartyroomId partyroomId;
    private final PlaybackId lastPlaybackId;
    private final CrewId lastDjCrewId;

    public PlaybackDeactivatedEvent(PartyroomId partyroomId, PlaybackId lastPlaybackId, CrewId lastDjCrewId) {
        this.partyroomId = partyroomId;
        this.lastPlaybackId = lastPlaybackId;
        this.lastDjCrewId = lastDjCrewId;
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(partyroomId.getId());
    }
}
