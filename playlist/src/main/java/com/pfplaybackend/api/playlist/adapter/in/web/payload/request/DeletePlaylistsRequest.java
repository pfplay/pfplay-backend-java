package com.pfplaybackend.api.playlist.adapter.in.web.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Schema(description = "List")
@Getter
public class DeletePlaylistsRequest {
    @Schema(description = "list Id", example = "[1,2,3]", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> playlistIds;
}
