package com.pfplaybackend.api.playlist.application.port.out;

import com.pfplaybackend.api.common.domain.value.PlaylistId;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.playlist.application.dto.PlaylistSummaryDto;
import com.pfplaybackend.api.playlist.application.dto.PlaylistTrackDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PlaylistQueryPort {
    List<PlaylistSummaryDto> findAllByUserId(UserId userId);
    PlaylistSummaryDto findByIdAndUserId(Long playlistId, UserId userId);
    Page<PlaylistTrackDto> getTracksWithPagination(PlaylistId playlistId, Pageable pageable);
}
