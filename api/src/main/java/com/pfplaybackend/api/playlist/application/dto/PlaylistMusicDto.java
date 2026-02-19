package com.pfplaybackend.api.playlist.application.dto;

import com.pfplaybackend.api.common.domain.value.UserId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PlaylistMusicDto {
    private Long trackId;
    private String linkId;
    private String name;
    private Integer orderNumber;
    private String duration;
    private String thumbnailImage;
}