package com.pfplaybackend.api.playlist.domain.event;

import com.pfplaybackend.api.common.domain.event.DomainEvent;
import lombok.Getter;

@Getter
public class TrackAddedEvent extends DomainEvent {
    private final Long playlistId;
    private final String linkId;

    public TrackAddedEvent(Long playlistId, String linkId) {
        this.playlistId = playlistId;
        this.linkId = linkId;
    }

    @Override
    public String getAggregateId() {
        return playlistId.toString();
    }
}
