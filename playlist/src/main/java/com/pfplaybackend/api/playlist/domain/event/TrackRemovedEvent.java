package com.pfplaybackend.api.playlist.domain.event;

import com.pfplaybackend.api.common.domain.event.DomainEvent;
import com.pfplaybackend.api.common.domain.value.PlaylistId;
import lombok.Getter;

@Getter
public class TrackRemovedEvent extends DomainEvent {
    private final PlaylistId playlistId;
    private final Long trackId;
    private final String linkId;
    private final String trackName;

    public TrackRemovedEvent(PlaylistId playlistId, Long trackId, String linkId, String trackName) {
        this.playlistId = playlistId;
        this.trackId = trackId;
        this.linkId = linkId;
        this.trackName = trackName;
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(playlistId.getId());
    }
}
