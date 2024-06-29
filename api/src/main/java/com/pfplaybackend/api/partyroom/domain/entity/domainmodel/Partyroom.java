package com.pfplaybackend.api.partyroom.domain.entity.domainmodel;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.partyroom.domain.enums.StageType;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.domain.value.PlaybackId;
import com.pfplaybackend.api.partyroom.domain.value.PlaylistId;
import com.pfplaybackend.api.partyroom.presentation.payload.request.CreatePartyroomRequest;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@SuperBuilder(toBuilder = true)
public class Partyroom {
    private PartyroomId partyroomId;
    private StageType stageType;
    private String title;
    private String introduction;
    private String linkDomain;
    private int playbackTimeLimit;
    private UserId hostId;
    private String noticeContent;
    private List<Partymember> partymembers;
    private List<Dj> djs;
    private PlaybackId currentPlaybackId;
    private boolean isPlaybackActivated;
    private boolean isQueueClosed;
    private boolean isTerminated;

    public Partyroom() {}

    public Partyroom(StageType stageType, UserId hostId, String title, String introduction,
                     String linkDomain, int playbackTimeLimit) {
        // The identifier of the new object has not been determined.
        this.partyroomId = null;
        // Assign from Parameters
        this.stageType = stageType;
        this.hostId = hostId;
        this.title = title;
        this.introduction = introduction;
        this.linkDomain = linkDomain;
        this.playbackTimeLimit = playbackTimeLimit;
        // Assign Default Values
        this.partymembers = new ArrayList<>();
        this.djs = new ArrayList<>();
        this.noticeContent = "";
        this.currentPlaybackId = null;
        this.isPlaybackActivated = false;
        this.isQueueClosed = false;
        this.isTerminated = false;
    }

    @Builder
    public Partyroom(PartyroomId partyroomId,  StageType stageType, String title, String introduction,
                     String linkDomain, int playbackTimeLimit,
                     UserId hostId, String noticeContent,
                     List<Partymember> partymembers, List<Dj> djs,
                     PlaybackId currentPlaybackId,
                     boolean isPlaybackActivated, boolean isQueueClosed, boolean isTerminated) {
        this.partyroomId = partyroomId;
        this.title = title;
        this.introduction = introduction;
        this.linkDomain = linkDomain;
        this.playbackTimeLimit = playbackTimeLimit;
        this.partymembers = new ArrayList<>();
        this.djs = new ArrayList<>();
        this.stageType = stageType;
        this.hostId = hostId;
        this.noticeContent = noticeContent;
        this.currentPlaybackId = currentPlaybackId;
        this.isPlaybackActivated = isPlaybackActivated;
        this.isQueueClosed = isQueueClosed;
        this.isTerminated = isTerminated;
    }

    public static Partyroom create(CreatePartyroomRequest request, StageType stageType, UserId hostId) {
        return new Partyroom(stageType, hostId, request.getTitle(), request.getIntroduction(),
                request.getLinkDomain(), request.getPlaybackTimeLimit());
    }

    public Partyroom assignPartymembers(List<Partymember> partymembers) {
        this.partymembers = partymembers;
        return this;
    }

    public Partyroom assignDjs(List<Dj> djs) {
        this.djs = djs;
        return this;
    }

    public Partyroom addNewPartymember(UserId userId, AuthorityTier authorityTier) {
        Partymember partymember = Partymember.create(userId, authorityTier);
        this.partymembers.add(partymember);
        System.out.println(this.partymembers);
        return this;
    }

    public Partyroom addPartymember(Partymember partymember) {
        this.partymembers.add(partymember);
        return this;
    }
}