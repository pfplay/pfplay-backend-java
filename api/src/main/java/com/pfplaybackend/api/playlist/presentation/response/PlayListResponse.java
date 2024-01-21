package com.pfplaybackend.api.playlist.presentation.response;

import com.pfplaybackend.api.playlist.enums.PlayListType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Schema
@Builder
public class PlayListResponse {
    private final Long id;
    private final Integer orderNumber;
    private final String name;
    private final PlayListType type;
    private final Long count;
}
