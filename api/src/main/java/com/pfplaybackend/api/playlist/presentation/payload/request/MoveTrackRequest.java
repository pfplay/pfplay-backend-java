package com.pfplaybackend.api.playlist.presentation.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "Move Track")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MoveTrackRequest {
    @Schema(description = "이동할 대상 플레이리스트 ID", example = "2", requiredMode = Schema.RequiredMode.REQUIRED, type = "integer")
    private Long targetPlaylistId;
}
