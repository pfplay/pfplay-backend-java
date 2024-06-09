package com.pfplaybackend.api.playlist.presentaion.dto.response;

import com.pfplaybackend.api.playlist.application.dto.SearchPlaylistMusicDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SearchPlaylistMusicResponse {
    private String nextPageToken;
    private List<SearchPlaylistMusicDto> musicList;
}