package com.pfplaybackend.api.playlist.presentation.payload.response;

import com.pfplaybackend.api.common.dto.PaginationDto;
import com.pfplaybackend.api.playlist.application.dto.PlaylistMusicDto;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
public class QueryMusicListResponse {
    private List<PlaylistMusicDto> content;
    private PaginationDto pagination;

    public static QueryMusicListResponse from(Page<PlaylistMusicDto> page) {
        return QueryMusicListResponse.builder()
                .content(page.getContent())
                .pagination(PaginationDto.builder()
                        .pageNumber(page.getNumber())
                        .pageSize(page.getSize())
                        .totalPages(page.getTotalPages())
                        .totalElements(page.getTotalElements())
                        .hasNext(page.hasNext())
                        .build())
                .build();
    }
}