package com.pfplaybackend.api.playlist.presentation.response;

import com.pfplaybackend.api.playlist.presentation.dto.PlayListDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PlayListResponse {
    private List<PlayListDto> data;
}