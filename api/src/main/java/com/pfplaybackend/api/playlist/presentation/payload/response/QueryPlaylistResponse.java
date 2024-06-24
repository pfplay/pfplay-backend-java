package com.pfplaybackend.api.playlist.presentation.payload.response;

import com.pfplaybackend.api.playlist.application.dto.PlaylistSummary;
import com.pfplaybackend.api.playlist.domain.enums.PlaylistType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Schema
@Builder
public class QueryPlaylistResponse {
    private final List<PlaylistSummary> playlists;
}
