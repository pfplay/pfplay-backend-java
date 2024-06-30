package com.pfplaybackend.api.partyroom.application.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;

@Data
public class PlaybackDto {
    private long id;
    private String linkId;
    private String name;
    private String duration;
    private String thumbnailImage;
    private LocalTime endTime;

    @QueryProjection
    public PlaybackDto(long id, String linkId, String name, String duration, String thumbnailImage) {
        this.id = id;
        this.linkId = linkId;
        this.name = name;
        this.duration = duration;
        this.thumbnailImage = thumbnailImage;
    }
}
