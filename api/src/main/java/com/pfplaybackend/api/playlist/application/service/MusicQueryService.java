package com.pfplaybackend.api.playlist.application.service;

import com.pfplaybackend.api.playlist.application.dto.PlaylistMusicDto;
import com.pfplaybackend.api.playlist.presentation.payload.response.QueryMusicListResponse;
import com.pfplaybackend.api.playlist.repository.PlaylistMusicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MusicQueryService {

    private final PlaylistMusicRepository playlistMusicRepository;

    public Page<PlaylistMusicDto> getMusics(Long playlistId, int pageNo, int pageSize) {
        System.out.println(playlistId);
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.ASC, "orderNumber"));
        return playlistMusicRepository.getMusicsWithPagination(playlistId, pageable);
    }
}