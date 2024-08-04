package com.pfplaybackend.api.partyroom.domain.entity.domainmodel;

import com.pfplaybackend.api.partyroom.application.dto.MusicDto;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.domain.value.PlaylistId;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

@Getter
public class Playback {

    private Long id;
    private PartyroomId partyroomId;
    private UserId userId;
    private String name;
    private String duration;
    private String linkId;
    private String thumbnailImage;
    private int grabCount;
    private int likeCount;
    private int dislikeCount;
    private Long endTime;

    public Playback() {}

    public Playback(PartyroomId partyroomId, UserId userId, String name,
                    String duration, String linkId, String thumbnailImage, Long endTime) {
        this.partyroomId = partyroomId;
        this.userId = userId;
        this.name = name;
        this.duration = duration;
        this.linkId = linkId;
        this.thumbnailImage = thumbnailImage;
        this.grabCount = 0;
        this.likeCount = 0;
        this.dislikeCount = 0;
        this.endTime = endTime;
    }

    @Builder
    public Playback(Long id, PartyroomId partyroomId, UserId userId, String name, String duration, String linkId, String thumbnailImage, int grabCount, int likeCount, int dislikeCount, Long endTime) {
        this.id = id;
        this.partyroomId = partyroomId;
        this.userId = userId;
        this.name = name;
        this.duration = duration;
        this.linkId = linkId;
        this.thumbnailImage = thumbnailImage;
        this.grabCount = grabCount;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.endTime = endTime;
    }

    public static Playback create(PartyroomId partyroomId, UserId userId,
                                  MusicDto musicDto) {

        return new Playback(partyroomId, userId,
                musicDto.getName(), musicDto.getDuration(),
                musicDto.getLinkId(), musicDto.getThumbnailImage(),
                calculateEndTime(musicDto.getDuration()));
    }

    private static long calculateEndTime(String duration) {
        Instant now = Instant.now();
        Duration parsed = parseDuration(duration);
        return now.plus(parsed).toEpochMilli();
    }

    private static Duration parseDuration(String durationStr) {
        String[] parts = durationStr.split(":");
        if (parts.length != 2) {
            throw new DateTimeParseException("Invalid duration format", durationStr, 0);
        }
        int minutes = Integer.parseInt(parts[0]);
        int seconds = Integer.parseInt(parts[1]);
        return Duration.ofMinutes(minutes).plusSeconds(seconds);
    }

    public Playback updateAggregation(int deltaLikeCount, int deltaDislikeCount, int deltaGrabCount) {
        this.likeCount += deltaLikeCount;
        this.grabCount += deltaGrabCount;
        this.dislikeCount += deltaDislikeCount;
        return this;
    }
}