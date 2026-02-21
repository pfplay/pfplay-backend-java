package com.pfplaybackend.api.admin.adapter.out.external;

import com.pfplaybackend.api.admin.application.port.out.AdminPlaylistPort;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.playlist.application.service.PlaylistCommandService;
import com.pfplaybackend.api.playlist.domain.entity.data.PlaylistData;
import com.pfplaybackend.api.playlist.domain.entity.data.TrackData;
import com.pfplaybackend.api.playlist.domain.enums.PlaylistType;
import com.pfplaybackend.api.playlist.adapter.out.persistence.PlaylistRepository;
import com.pfplaybackend.api.playlist.adapter.out.persistence.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminPlaylistAdapter implements AdminPlaylistPort {

    private final PlaylistRepository playlistRepository;
    private final TrackRepository trackRepository;
    private final PlaylistCommandService playlistCommandService;

    @Override
    public PlaylistData savePlaylist(PlaylistData playlist) {
        return playlistRepository.save(playlist);
    }

    @Override
    public TrackData saveTrack(TrackData track) {
        return trackRepository.save(track);
    }

    @Override
    public List<PlaylistData> findPlaylistsByOwnerAndType(UserId ownerId, PlaylistType type) {
        return playlistRepository.findByOwnerIdAndTypeOrderByOrderNumberDesc(ownerId, type);
    }

    @Override
    public void createDefaultPlaylist(UserId userId) {
        playlistCommandService.createDefaultPlaylist(userId);
    }
}
