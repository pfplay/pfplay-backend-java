package com.pfplaybackend.api.playlist.domain.entity.domainmodel;

import com.pfplaybackend.api.playlist.domain.entity.data.PlaylistData;
import com.pfplaybackend.api.playlist.domain.enums.PlaylistType;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Playlist {
    private long id;
    private final String name;
    private final UserId ownerId;
    private final PlaylistType type;
    private final Integer orderNumber;

    @Builder
    public Playlist(long id, Integer orderNumber, String name, PlaylistType type, UserId ownerId) {
        this.id = id;
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
        return Playlist.builder()
                .type(type)
                .name(name)
                .ownerId(ownerId)
                .orderNumber(orderNumber)
                .build();
    }

    public Playlist rename(String name) {
        return Playlist.builder()
                .id(this.id)
                .name(name)
                .ownerId(this.ownerId)
                .type(this.type)
                .orderNumber(this.orderNumber)
                .build();
    }
}
