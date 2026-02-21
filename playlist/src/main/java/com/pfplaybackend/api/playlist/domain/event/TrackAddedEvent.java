package com.pfplaybackend.api.playlist.domain.event;

import com.pfplaybackend.api.common.domain.event.DomainEvent;
import com.pfplaybackend.api.common.domain.value.PlaylistId;
import lombok.Getter;

@Getter
public class TrackAddedEvent extends DomainEvent {
    private final PlaylistId playlistId;
    private final String linkId;
    private final String trackName;

    public TrackAddedEvent(PlaylistId playlistId, String linkId, String trackName) {
        this.playlistId = playlistId;
        this.linkId = linkId;
        this.trackName = trackName;
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(playlistId.getId());
    }
}
