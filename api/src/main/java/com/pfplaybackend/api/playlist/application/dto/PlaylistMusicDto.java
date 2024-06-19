package com.pfplaybackend.api.playlist.application.dto;

import com.pfplaybackend.api.user.domain.model.value.UserId;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaylistMusicDto {
    private Long musicId;
    private UserId ownerId;
    private Integer orderNumber;
    private String name;
    private String duration;
    private String thumbnailImage;
}