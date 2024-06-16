package com.pfplaybackend.api.playlist.presentation.dto.response;

import com.pfplaybackend.api.playlist.application.dto.PlaylistMusicDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PlaylistMusicResponse {
    private List<PlaylistMusicDto> musicList;
    private int totalPage;
}