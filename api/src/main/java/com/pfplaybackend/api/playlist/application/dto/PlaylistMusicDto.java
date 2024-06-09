package com.pfplaybackend.api.playlist.application.dto;

import com.pfplaybackend.api.user.model.value.UserId;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaylistMusicDto {
    private Long musicId;
    private UserId uid;
    private Integer orderNumber;
    private String name;
    private String duration;
    private String thumbnailImage;
}