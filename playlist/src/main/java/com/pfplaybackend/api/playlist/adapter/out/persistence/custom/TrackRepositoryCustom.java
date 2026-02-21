package com.pfplaybackend.api.playlist.adapter.out.persistence.custom;

import com.pfplaybackend.api.common.domain.value.PlaylistId;
import com.pfplaybackend.api.playlist.application.dto.PlaylistTrackDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TrackRepositoryCustom {
    Page<PlaylistTrackDto> getTracksWithPagination(PlaylistId playlistId, Pageable pageable);
}
