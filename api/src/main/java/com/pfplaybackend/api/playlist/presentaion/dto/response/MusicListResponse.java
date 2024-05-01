package com.pfplaybackend.api.playlist.presentaion.dto.response;

import com.pfplaybackend.api.playlist.application.dto.MusicListDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MusicListResponse {
    private List<MusicListDto> musicList;
    private int totalPage;
}