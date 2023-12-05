package com.pfplaybackend.api.youtube.presentation.response;

import com.pfplaybackend.api.playlist.presentation.dto.PlayListDto;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.util.List;

@Getter
@Builder
public class MusicListResponse {
    private String id;
    private String thumbnailLow;
    private String thumbnailMedium;
    private String thumbnailHigh;
    private String title;
    private String duration;
    private String nextPageToken;
}