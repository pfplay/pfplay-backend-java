package com.pfplaybackend.api.playlist.presentaion.dto.response;

import com.pfplaybackend.api.playlist.application.dto.SearchMusicListDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SearchMusicListResponse {
    private String nextPageToken;
    private List<SearchMusicListDto> musicList;
}