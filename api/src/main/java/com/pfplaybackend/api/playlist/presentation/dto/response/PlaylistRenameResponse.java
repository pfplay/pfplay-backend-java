package com.pfplaybackend.api.playlist.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Schema
@Builder
public class PlaylistRenameResponse {
    private final Long id;
    private final String name;
}
