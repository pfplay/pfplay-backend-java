package com.pfplaybackend.api.partyroom.domain.entity.domainmodel;

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
    private String musicName;
    private int grabCount;
    private int likeCount;
    private int dislikeCount;
    private LocalTime endTime;

    public Playback() {}
}
