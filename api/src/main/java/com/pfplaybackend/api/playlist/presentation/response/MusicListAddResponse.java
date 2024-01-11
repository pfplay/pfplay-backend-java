package com.pfplaybackend.api.playlist.presentation.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MusicListAddResponse {
    private Long playListId;
    private Long musicId;
    private Integer orderNumber;
    private String name;
    private String duration;
}