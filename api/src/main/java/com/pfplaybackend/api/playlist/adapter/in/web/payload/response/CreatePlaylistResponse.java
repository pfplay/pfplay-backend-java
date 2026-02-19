package com.pfplaybackend.api.playlist.adapter.in.web.payload.response;

import com.pfplaybackend.api.playlist.domain.entity.data.PlaylistData;
import com.pfplaybackend.api.playlist.domain.enums.PlaylistType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreatePlaylistResponse {
    private final Long id;
    private final String name;
    private final Integer orderNumber;
    private final PlaylistType type;

    public static CreatePlaylistResponse from(PlaylistData playlist) {
        return CreatePlaylistResponse.builder()
                .id(playlist.getId())
                .name(playlist.getName())
                .orderNumber(playlist.getOrderNumber())
                .type(playlist.getType())
                .build();
    }
}
