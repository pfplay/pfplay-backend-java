package com.pfplaybackend.api.playlist.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.party.application.dto.playback.MusicDto;
import com.pfplaybackend.api.playlist.application.aspect.context.PlaylistContext;
import com.pfplaybackend.api.playlist.application.dto.PlaylistMusicDto;
import com.pfplaybackend.api.playlist.domain.entity.data.PlaylistData;
import com.pfplaybackend.api.playlist.domain.exception.PlaylistException;
import com.pfplaybackend.api.playlist.repository.TrackRepository;
import com.pfplaybackend.api.playlist.repository.PlaylistRepository;
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

    public Page<PlaylistMusicDto> getMusics(Long playlistId, int pageNo, int pageSize) {
        // 접근코자 하는 플레이리스트에 대한 소유자가 맞는가?
        PlaylistContext playlistContext = (PlaylistContext) ThreadLocalContext.getContext();
        Optional<PlaylistData> playlistOptional = playlistRepository.findByIdAndOwnerId(playlistId, playlistContext.getUserId());
        if(playlistOptional.isEmpty()) throw ExceptionCreator.create(PlaylistException.NOT_FOUND_PLAYLIST);

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.ASC, "orderNumber"));
        return trackRepository.getMusicsWithPagination(playlistId, pageable);
    }

    @Transactional
    public MusicDto getFirstMusic(Long playlistId) {
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "orderNumber"));
        Page<PlaylistMusicDto> page = trackRepository.getMusicsWithPagination(playlistId, pageable);
        updateRotateOrderNumber(playlistId, page.getTotalElements());
        PlaylistMusicDto dto = page.getContent().get(0);
        return new MusicDto(
                dto.getLinkId(),
                dto.getName(),
                dto.getThumbnailImage(),
                dto.getDuration(),
                dto.getOrderNumber()
        );
    }

    @Transactional
    public boolean isEmptyPlaylist(Long playlistId) {
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "orderNumber"));
        Page<PlaylistMusicDto> page = trackRepository.getMusicsWithPagination(playlistId, pageable);
        return page.getTotalElements() == 0;
    }

    public void updateRotateOrderNumber(Long playlistId, long totalCount) {
        trackRepository.reorderMusics(playlistId, totalCount);
    }
}