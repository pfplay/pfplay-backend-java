package com.pfplaybackend.api.playlist.adapter.in.web.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Schema
@Builder
public class DeletePlaylistsResponse {
    private final List<Long> listIds;

    public static DeletePlaylistsResponse from(List<Long> listIds) {
        return DeletePlaylistsResponse.builder()
                .listIds(listIds)
                .build();
    }
}
