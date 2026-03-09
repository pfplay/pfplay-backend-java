package com.pfplaybackend.api.playlist.adapter.in.web.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.List;

@Schema(description = "List")
@Getter
public class DeletePlaylistsRequest {
    @NotEmpty(message = "playlistIds must not be empty.")
    @Schema(description = "list Id", example = "[1,2,3]", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> playlistIds;
}
