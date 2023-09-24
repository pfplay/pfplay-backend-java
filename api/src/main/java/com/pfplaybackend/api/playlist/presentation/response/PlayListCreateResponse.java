package com.pfplaybackend.api.playlist.presentation.response;

import com.pfplaybackend.api.entity.PlayList;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlayListCreateResponse {
    private Long id;
    private String name;

    public static PlayListCreateResponse toResponse(PlayList playList) {
        return PlayListCreateResponse
                .builder()
                .id(playList.getId())
                .name(playList.getName())
                .build();
    }
}
