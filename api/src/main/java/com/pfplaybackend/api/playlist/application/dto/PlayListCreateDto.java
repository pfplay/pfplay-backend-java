package com.pfplaybackend.api.playlist.application.dto;

import com.pfplaybackend.api.playlist.model.entity.Playlist;
import com.pfplaybackend.api.playlist.model.enums.PlaylistType;
import com.pfplaybackend.api.user.model.value.UserId;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlayListCreateDto {
    private final Playlist playList;
    private final Integer orderNumber;
    private final String name;
    private final PlaylistType type;
    private final UserId userId;

    public Playlist toEntity() {
        return Playlist.builder()
                .orderNumber(orderNumber)
                .name(name)
                .userId(userId)
                .type(type)
                .build();
    }
}
