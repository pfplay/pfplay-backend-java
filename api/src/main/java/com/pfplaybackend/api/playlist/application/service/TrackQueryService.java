package com.pfplaybackend.api.playlist.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.playlist.application.dto.PlaylistTrackDto;
import com.pfplaybackend.api.playlist.domain.entity.data.PlaylistData;
import com.pfplaybackend.api.playlist.domain.exception.PlaylistException;
import com.pfplaybackend.api.playlist.adapter.out.persistence.TrackRepository;
import com.pfplaybackend.api.playlist.adapter.out.persistence.PlaylistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TrackQueryService {

    private final PlaylistRepository playlistRepository;
    private final TrackRepository trackRepository;

    @Transactional(readOnly = true)
    public Page<PlaylistTrackDto> getTracks(Long playlistId, int pageNo, int pageSize) {
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        Optional<PlaylistData> playlistOptional = playlistRepository.findByIdAndOwnerId(playlistId, authContext.getUserId());
        if(playlistOptional.isEmpty()) throw ExceptionCreator.create(PlaylistException.NOT_FOUND_PLAYLIST);

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.ASC, "orderNumber"));
        return trackRepository.getTracksWithPagination(playlistId, pageable);
    }

    @Transactional(readOnly = true)
    public boolean isEmptyPlaylist(Long playlistId) {
        return !trackRepository.existsByPlaylistDataId(playlistId);
    }
}
