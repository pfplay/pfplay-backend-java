package com.pfplaybackend.api.playlist.presentation.payload.response;

import com.pfplaybackend.api.playlist.domain.entity.domainmodel.Playlist;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Schema
@Builder
public class UpdatePlaylistResponse {
    private final Long id;
    private final String name;

    public static UpdatePlaylistResponse from(Playlist playlist) {
        return UpdatePlaylistResponse.builder()
                .id(playlist.getId())
                .name(playlist.getName())
                .build();
    }
}
