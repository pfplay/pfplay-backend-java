package com.pfplaybackend.api.partyroom.presentation.payload.response;

import com.pfplaybackend.api.partyroom.application.dto.PlaybackHistoryDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryPlaybackHistoryResponse {

    List<PlaybackHistoryDto> playbackHistory;

    public static QueryPlaybackHistoryResponse from(List<PlaybackHistoryDto> list) {
        return new QueryPlaybackHistoryResponse(list);
    }
}
