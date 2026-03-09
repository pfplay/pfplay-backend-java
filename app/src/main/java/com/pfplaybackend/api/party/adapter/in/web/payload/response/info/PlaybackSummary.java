package com.pfplaybackend.api.party.adapter.in.web.payload.response.info;

import com.pfplaybackend.api.party.application.dto.playback.PlaybackDto;
import com.pfplaybackend.api.party.domain.entity.data.PlaybackData;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "재생 요약 정보")
public record PlaybackSummary(
        @Schema(description = "곡 이름") String name,
        @Schema(description = "썸네일 이미지 URL") String thumbnailImage,
        @Schema(description = "재생 시간") String duration
) {
    public static PlaybackSummary from(PlaybackData playback) {
        return new PlaybackSummary(
                playback.getName(),
                playback.getThumbnailImage(),
                playback.getDuration().toDisplayString()
        );
    }

    public static PlaybackSummary withoutDuration(PlaybackData playback) {
        return new PlaybackSummary(
                playback.getName(),
                playback.getThumbnailImage(),
                null
        );
    }

    public static PlaybackSummary withoutDuration(PlaybackDto playback) {
        return new PlaybackSummary(
                playback.getName(),
                playback.getThumbnailImage(),
                null
        );
    }
}
