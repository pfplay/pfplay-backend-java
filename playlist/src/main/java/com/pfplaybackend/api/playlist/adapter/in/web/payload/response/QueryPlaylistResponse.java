package com.pfplaybackend.api.playlist.adapter.in.web.payload.response;

import com.pfplaybackend.api.playlist.application.dto.PlaylistSummaryDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Schema
@Builder
public class QueryPlaylistResponse {
    private final List<PlaylistSummaryDto> playlists;
}