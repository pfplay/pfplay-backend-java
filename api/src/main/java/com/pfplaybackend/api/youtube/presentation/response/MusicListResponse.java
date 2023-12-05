package com.pfplaybackend.api.youtube.presentation.response;

import com.pfplaybackend.api.youtube.presentation.dto.MusicList;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MusicListResponse {
    private String nextPageToken;
    private List<MusicList> musicList;
}