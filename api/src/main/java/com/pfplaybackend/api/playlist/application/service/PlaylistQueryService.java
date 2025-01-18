package com.pfplaybackend.api.playlist.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.playlist.application.aspect.context.PlaylistContext;
import com.pfplaybackend.api.playlist.application.dto.PlaylistSummary;
import com.pfplaybackend.api.playlist.repository.PlaylistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 플레이리스트 CRUD
 */
@Service
@RequiredArgsConstructor
public class PlaylistQueryService {

    private final PlaylistRepository playlistRepository;

    @Transactional(readOnly = true)
    public List<PlaylistSummary> getPlaylists() {
        PlaylistContext playlistContext = (PlaylistContext) ThreadLocalContext.getContext();
        return playlistRepository.findAllByUserId(playlistContext.getUserId());
    }

    @Transactional(readOnly = true)
    public PlaylistSummary getPlaylist(Long playlistId) {
        PlaylistContext playlistContext = (PlaylistContext) ThreadLocalContext.getContext();
        return playlistRepository.findByIdAndUserId(playlistId, playlistContext.getUserId());
    }
}