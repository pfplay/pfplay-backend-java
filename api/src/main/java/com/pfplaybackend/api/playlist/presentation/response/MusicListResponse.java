package com.pfplaybackend.api.playlist.presentation.response;

import com.pfplaybackend.api.playlist.presentation.dto.MusicListDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MusicListResponse {
    private String nextPageToken;
    private List<MusicListDto> musicList;
}