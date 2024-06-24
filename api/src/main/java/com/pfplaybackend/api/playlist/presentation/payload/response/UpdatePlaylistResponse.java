package com.pfplaybackend.api.playlist.presentation.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Schema
@Builder
public class UpdatePlaylistResponse {
    private final Long id;
    private final String name;
}
