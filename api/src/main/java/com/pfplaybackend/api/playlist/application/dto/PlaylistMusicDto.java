package com.pfplaybackend.api.playlist.application.dto;

import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PlaylistMusicDto {
    private String linkId;
    private String name;
    private Integer orderNumber;
    private String duration;
    private String thumbnailImage;
}