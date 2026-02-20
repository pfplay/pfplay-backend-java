package com.pfplaybackend.api.party.application.dto.playback;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PlaybackDto {
    private long id;
    private String linkId;
    private String name;
    private String duration;
    private String thumbnailImage;
    private long endTime;

    @QueryProjection
    public PlaybackDto(long id, String linkId, String name, String duration, String thumbnailImage) {
        this.id = id;
        this.linkId = linkId;
        this.name = name;
        this.duration = duration;
        this.thumbnailImage = thumbnailImage;
    }

    public static PlaybackDto withEndTime(long id, String linkId, String name,
                                           String duration, String thumbnailImage, long endTime) {
        PlaybackDto dto = new PlaybackDto(id, linkId, name, duration, thumbnailImage);
        dto.setEndTime(endTime);
        return dto;
    }
}
