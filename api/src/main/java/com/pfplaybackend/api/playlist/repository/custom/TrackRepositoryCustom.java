package com.pfplaybackend.api.playlist.repository.custom;

import com.pfplaybackend.api.playlist.application.dto.PlaylistMusicDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TrackRepositoryCustom {
    Page<PlaylistMusicDto> getMusicsWithPagination(Long playlistId, Pageable pageable);
}