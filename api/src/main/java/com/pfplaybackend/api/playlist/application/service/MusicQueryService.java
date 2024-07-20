package com.pfplaybackend.api.playlist.application.service;

import com.pfplaybackend.api.partyroom.application.dto.MusicDto;
import com.pfplaybackend.api.partyroom.domain.value.PlaylistId;
import com.pfplaybackend.api.playlist.application.dto.PlaylistMusicDto;
import com.pfplaybackend.api.playlist.domain.entity.data.PlaylistMusicData;
import com.pfplaybackend.api.playlist.presentation.payload.response.QueryMusicListResponse;
import com.pfplaybackend.api.playlist.repository.PlaylistMusicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MusicQueryService {

    private final PlaylistMusicRepository playlistMusicRepository;

    public Page<PlaylistMusicDto> getMusics(Long playlistId, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.ASC, "orderNumber"));
        return playlistMusicRepository.getMusicsWithPagination(playlistId, pageable);
    }

    @Transactional
    public MusicDto getFirstMusic(Long playlistId) {
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "orderNumber"));
        Page<PlaylistMusicDto> page = playlistMusicRepository.getMusicsWithPagination(playlistId, pageable);
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
        Page<PlaylistMusicDto> page = playlistMusicRepository.getMusicsWithPagination(playlistId, pageable);
        return page.getTotalElements() == 0;
    }

    public void updateRotateOrderNumber(Long playlistId, long totalCount) {
        playlistMusicRepository.reorderMusics(playlistId, totalCount);
    }
}