package com.pfplaybackend.api.admin.application.port.out;

import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.playlist.domain.entity.data.PlaylistData;
import com.pfplaybackend.api.playlist.domain.entity.data.TrackData;
import com.pfplaybackend.api.playlist.domain.enums.PlaylistType;

import java.util.List;

public interface AdminPlaylistPort {
    PlaylistData savePlaylist(PlaylistData playlist);
    TrackData saveTrack(TrackData track);
    List<PlaylistData> findPlaylistsByOwnerAndType(UserId ownerId, PlaylistType type);
    void createDefaultPlaylist(UserId userId);
}
