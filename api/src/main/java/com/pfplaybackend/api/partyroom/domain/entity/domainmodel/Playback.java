package com.pfplaybackend.api.partyroom.domain.entity.domainmodel;

import com.pfplaybackend.api.partyroom.application.dto.MusicDto;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.user.domain.value.UserId;
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

    public static Playback create(PartyroomId partyroomId, UserId userId,
                                  MusicDto musicDto) {
        return new Playback(partyroomId, userId,
                musicDto.getName(), musicDto.getDuration(),
                musicDto.getLinkId(), musicDto.getThumbnailImage());
    }
}