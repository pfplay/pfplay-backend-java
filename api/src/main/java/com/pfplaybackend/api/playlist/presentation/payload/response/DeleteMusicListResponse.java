package com.pfplaybackend.api.playlist.presentation.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Schema
@Builder
public class DeleteMusicListResponse {
    private final List<Long> listIds;
}
