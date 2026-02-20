package com.pfplaybackend.api.playlist.adapter.in.web.payload.response;

import com.pfplaybackend.api.common.dto.PaginationDto;
import com.pfplaybackend.api.playlist.application.dto.PlaylistTrackDto;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
public class QueryTrackListResponse {
    private List<PlaylistTrackDto> content;
    private PaginationDto pagination;

    public static QueryTrackListResponse from(Page<PlaylistTrackDto> page) {
        return QueryTrackListResponse.builder()
                .content(page.getContent())
                .pagination(new PaginationDto(
                        page.getNumber(),
                        page.getSize(),
                        page.getTotalPages(),
                        page.getTotalElements(),
                        page.hasNext()
                ))
                .build();
    }
}