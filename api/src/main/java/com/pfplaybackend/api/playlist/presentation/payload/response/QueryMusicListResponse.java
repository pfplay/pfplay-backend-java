package com.pfplaybackend.api.playlist.presentation.payload.response;

import com.pfplaybackend.api.playlist.application.dto.PlaylistMusicDto;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
public class QueryMusicListResponse {
    private List<PlaylistMusicDto> musicList;
    private int totalPage;
    private int totalElements;

    public static QueryMusicListResponse from(Page<PlaylistMusicDto> page) {
        return QueryMusicListResponse.builder()
                .musicList(page.getContent())
                .totalPage(page.getTotalPages())
                .totalElements((int) page.getTotalElements())
                .build();
    }
}