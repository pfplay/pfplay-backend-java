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
    private final UserId ownerId;

    public Playlist(Integer orderNumber, String name, PlaylistType type, UserId ownerId) {
        this.orderNumber = orderNumber;
        this.name = name;
        this.type = type;
        this.ownerId = ownerId;
    }

    public PlaylistData toData() {
        return PlaylistData.builder()
                .orderNumber(orderNumber)
                .name(name)
                .type(type)
                .ownerId(ownerId)
                .build();
    }

    static public Playlist create(Integer orderNumber, String name, PlaylistType type, UserId ownerId) {
        return new Playlist(orderNumber, name, type, ownerId);
    }

}
