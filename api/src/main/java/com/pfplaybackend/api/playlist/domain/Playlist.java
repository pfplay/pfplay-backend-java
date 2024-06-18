package com.pfplaybackend.api.playlist.domain;

import com.pfplaybackend.api.playlist.domain.model.entity.PlaylistData;
import com.pfplaybackend.api.playlist.domain.model.enums.PlaylistType;
import com.pfplaybackend.api.user.domain.model.value.UserId;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Playlist {
    private final Integer orderNumber;
    private final String name;
    private final PlaylistType type;
    private final UserId userId;

    public Playlist(Integer orderNumber, String name, PlaylistType type, UserId userId) {
        this.orderNumber = orderNumber;
        this.name = name;
        this.type = type;
        this.userId = userId;
    }

    public PlaylistData toData() {
        return PlaylistData.builder()
                .orderNumber(orderNumber)
                .name(name)
                .type(type)
                .userId(userId)
                .build();
    }

    static public Playlist create(Integer orderNumber, String name, PlaylistType type, UserId userId) {
        return new Playlist(orderNumber, name, type, userId);
    }

}
