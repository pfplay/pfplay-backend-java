package com.pfplaybackend.api.playlist.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.playlist.application.dto.PlaylistTrackDto;
import com.pfplaybackend.api.playlist.application.port.out.PlaylistQueryPort;
import com.pfplaybackend.api.playlist.domain.exception.PlaylistException;
import com.pfplaybackend.api.playlist.domain.port.PlaylistAggregatePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class TrackQueryService {

    private final PlaylistAggregatePort aggregatePort;
    private final PlaylistQueryPort queryPort;

    @Transactional(readOnly = true)
    public Page<PlaylistTrackDto> getTracks(Long playlistId, int pageNo, int pageSize) {
        AuthContext authContext = ThreadLocalContext.getAuthContext();
        aggregatePort.findPlaylistByIdAndOwner(playlistId, authContext.getUserId())
                .orElseThrow(() -> ExceptionCreator.create(PlaylistException.NOT_FOUND_PLAYLIST));

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.ASC, "orderNumber"));
        return queryPort.getTracksWithPagination(playlistId, pageable);
    }

    @Transactional(readOnly = true)
    public boolean isEmptyPlaylist(Long playlistId) {
        return !aggregatePort.hasTracksByPlaylist(playlistId);
    }
}
