package com.pfplaybackend.api.partyroom.domain.entity.domainmodel;

import com.pfplaybackend.api.partyroom.application.dto.MusicDto;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.domain.value.PlaylistId;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Setter
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
    private LocalTime endTime;

    public Playback() {}

    public Playback(PartyroomId partyroomId, UserId userId, String name,
                    String duration, String linkId, String thumbnailImage) {
        this.partyroomId = partyroomId;
        this.userId = userId;
        this.name = name;
        this.duration = duration;
        this.linkId = linkId;
        this.thumbnailImage = thumbnailImage;
        this.grabCount = 0;
        this.likeCount = 0;
        this.dislikeCount = 0;
    }

    @Builder
    public Playback(Long id, PartyroomId partyroomId, UserId userId, String name, String duration, String linkId, String thumbnailImage, int grabCount, int likeCount, int dislikeCount, LocalTime endTime) {
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
                musicDto.getLinkId(), musicDto.getThumbnailImage());
    }

    public Playback updateAggregation(int deltaLikeCount, int deltaDislikeCount, int deltaGrabCount) {
        this.likeCount += deltaLikeCount;
        this.grabCount += deltaGrabCount;
        this.dislikeCount += deltaDislikeCount;
        return this;
    }
}