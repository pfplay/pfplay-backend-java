package com.pfplaybackend.api.playlist.application.dto;

import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PlaylistMusicDto {
    private Long musicId;
    private UserId ownerId;
    private Integer orderNumber;
    private String name;
    private String duration;
    private String thumbnailImage;
}