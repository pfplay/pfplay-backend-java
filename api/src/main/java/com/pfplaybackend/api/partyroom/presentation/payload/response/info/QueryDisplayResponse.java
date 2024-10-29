package com.pfplaybackend.api.partyroom.presentation.payload.response.info;

import com.pfplaybackend.api.partyroom.application.dto.playback.DisplayDto;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class QueryDisplayResponse {
    private DisplayDto display;

    public static QueryDisplayResponse from(DisplayDto display) {
        return new QueryDisplayResponse(display);
    }
}
