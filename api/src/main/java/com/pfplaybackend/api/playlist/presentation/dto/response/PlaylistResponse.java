package com.pfplaybackend.api.playlist.presentation.dto.response;

import com.pfplaybackend.api.playlist.model.enums.PlaylistType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Schema
@Builder
public class PlaylistResponse {
    private final Long id;
    private final Integer orderNumber;
    private final String name;
    private final PlaylistType type;
    private final Long count;
}
