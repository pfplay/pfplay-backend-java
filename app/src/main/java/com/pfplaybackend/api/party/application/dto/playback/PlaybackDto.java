package com.pfplaybackend.api.party.application.dto.playback;

import com.pfplaybackend.api.common.domain.value.Duration;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PlaybackDto {
    @Schema(example = "1") private long id;
    @Schema(example = "dQw4w9WgXcQ") private String linkId;
    @Schema(example = "Never Gonna Give You Up") private String name;
    @Schema(type = "string", example = "3:33") private Duration duration;
    @Schema(example = "https://i.ytimg.com/vi/dQw4w9WgXcQ/default.jpg") private String thumbnailImage;
    @Schema(example = "1700000000000") private long endTime;

    @QueryProjection
    public PlaybackDto(long id, String linkId, String name, Duration duration, String thumbnailImage) {
        this.id = id;
        this.linkId = linkId;
        this.name = name;
        this.duration = duration;
        this.thumbnailImage = thumbnailImage;
    }

    public static PlaybackDto withEndTime(long id, String linkId, String name,
                                           Duration duration, String thumbnailImage, long endTime) {
        PlaybackDto dto = new PlaybackDto(id, linkId, name, duration, thumbnailImage);
        dto.setEndTime(endTime);
        return dto;
    }
}
