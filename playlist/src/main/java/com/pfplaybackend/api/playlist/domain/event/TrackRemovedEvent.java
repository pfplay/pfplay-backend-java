package com.pfplaybackend.api.playlist.domain.event;

import com.pfplaybackend.api.common.domain.event.DomainEvent;
import lombok.Getter;

@Getter
public class TrackRemovedEvent extends DomainEvent {
    private final Long playlistId;
    private final Long trackId;

    public TrackRemovedEvent(Long playlistId, Long trackId) {
        this.playlistId = playlistId;
        this.trackId = trackId;
    }

    @Override
    public String getAggregateId() {
        return playlistId.toString();
    }
}
