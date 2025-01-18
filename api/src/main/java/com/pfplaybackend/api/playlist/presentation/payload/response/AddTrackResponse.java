package com.pfplaybackend.api.playlist.presentation.payload.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddTrackResponse {
    private Long playlistId;
    private Long trackId;
    private Integer orderNumber;
    private String name;
    private String duration;
}