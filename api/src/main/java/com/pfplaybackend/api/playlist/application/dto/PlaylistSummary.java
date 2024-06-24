package com.pfplaybackend.api.playlist.application.dto;

import com.pfplaybackend.api.playlist.domain.enums.PlaylistType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlaylistSummary {
    private final Long id;
    private final String name;
    private final Integer orderNumber;
    private final PlaylistType type;
    private final Long musicCount;
}
