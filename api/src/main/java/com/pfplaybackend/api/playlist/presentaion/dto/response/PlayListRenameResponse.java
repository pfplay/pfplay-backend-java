package com.pfplaybackend.api.playlist.presentaion.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Schema
@Builder
public class PlayListRenameResponse {
    private final Long id;
    private final String name;
}
