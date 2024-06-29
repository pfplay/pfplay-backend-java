package com.pfplaybackend.api.partyroom.application.dto;

import java.time.LocalTime;

public class PlaybackDto {
    private long playbackId;
    private String linkId;
    private String name;
    private String duration;
    private String thumbnailImage;
    private LocalTime endTime;
}
