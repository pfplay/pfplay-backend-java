package com.pfplaybackend.api.playlist.presentation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MusicListDto {
    private Long musicId;
    private String uid;
    private Integer orderNumber;
    private String name;
    private String duration;
    private String thumbnailImage;
}