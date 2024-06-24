package com.pfplaybackend.api.playlist.presentation.payload.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddMusicResponse {
    private Long playlistId;
    private Long musicId;
    private Integer orderNumber;
    private String name;
    private String duration;
}