package com.pfplaybackend.api.playlist.presentation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MusicListDto {
    private String id;
    private String thumbnailLow;
    private String thumbnailMedium;
    private String thumbnailHigh;
    private String title;
    private String duration;
}