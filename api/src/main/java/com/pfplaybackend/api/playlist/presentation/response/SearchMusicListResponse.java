package com.pfplaybackend.api.playlist.presentation.response;

import com.pfplaybackend.api.playlist.presentation.dto.SearchMusicListDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SearchMusicListResponse {
    private String nextPageToken;
    private List<SearchMusicListDto> musicList;
}