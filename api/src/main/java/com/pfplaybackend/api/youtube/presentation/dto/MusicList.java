package com.pfplaybackend.api.youtube.presentation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MusicList {
    private String id;
    private String thumbnailLow;
    private String thumbnailMedium;
    private String thumbnailHigh;
    private String title;
    private String duration;
}
