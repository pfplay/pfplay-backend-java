package com.pfplaybackend.api.playlist.adapter.out.persistence.custom;

import com.pfplaybackend.api.playlist.application.dto.PlaylistTrackDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TrackRepositoryCustom {
    Page<PlaylistTrackDto> getTracksWithPagination(Long playlistId, Pageable pageable);
}
