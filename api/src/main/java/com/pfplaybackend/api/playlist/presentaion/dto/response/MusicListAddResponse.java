package com.pfplaybackend.api.playlist.presentaion.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MusicListAddResponse {
    private Long playlistId;
    private Long musicId;
    private Integer orderNumber;
    private String name;
    private String duration;
}