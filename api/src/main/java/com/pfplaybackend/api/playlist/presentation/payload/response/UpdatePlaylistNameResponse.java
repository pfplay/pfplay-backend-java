package com.pfplaybackend.api.playlist.presentation.payload.response;

import com.pfplaybackend.api.playlist.domain.entity.data.PlaylistData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Schema
@Builder
public class UpdatePlaylistNameResponse {
    private final Long id;
    private final String name;

    public static UpdatePlaylistNameResponse from(PlaylistData playlist) {
        return UpdatePlaylistNameResponse.builder()
                .id(playlist.getId())
                .name(playlist.getName())
                .build();
    }
}
