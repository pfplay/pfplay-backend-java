package com.pfplaybackend.api.playlist.application.service;

import com.pfplaybackend.api.playlist.application.dto.PlaylistSummary;
import com.pfplaybackend.api.playlist.domain.entity.data.PlaylistData;
import com.pfplaybackend.api.playlist.domain.entity.data.PlaylistMusicData;
import com.pfplaybackend.api.playlist.domain.enums.PlaylistType;
import com.pfplaybackend.api.playlist.presentation.payload.request.AddMusicRequest;
import com.pfplaybackend.api.playlist.repository.PlaylistMusicRepository;
import com.pfplaybackend.api.playlist.repository.PlaylistRepository;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GrabMusicService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistMusicRepository playlistMusicRepository;
    private final MusicCommandService musicCommandService;

    @Transactional
    public void grabMusic(UserId userId, String linkId) {
        // LinkId cannot be duplicated.
        PlaylistMusicData targetPlaylistMusicData = playlistMusicRepository.findFirstByLinkId(linkId);
        PlaylistData playlistData = playlistRepository.findByOwnerIdAndType(userId, PlaylistType.GRABLIST);
        AddMusicRequest request = new AddMusicRequest(
                targetPlaylistMusicData.getName(),
                targetPlaylistMusicData.getLinkId(),
                targetPlaylistMusicData.getDuration(),
                targetPlaylistMusicData.getThumbnailImage()
        );
        musicCommandService.addMusicInPlaylist(playlistData.getId(), request);
    }
}
