package com.pfplaybackend.api.playlist.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchMusicListDto {
    private String id;
    private String thumbnailLow;
    private String thumbnailMedium;
    private String thumbnailHigh;
    private String title;
    private String duration;
}