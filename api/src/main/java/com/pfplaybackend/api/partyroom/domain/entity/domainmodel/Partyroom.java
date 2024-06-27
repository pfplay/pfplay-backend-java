package com.pfplaybackend.api.partyroom.domain.entity.domainmodel;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.partyroom.domain.enums.StageType;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.domain.value.PlaylistId;
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
    private PlaylistId currentPlaybackId;
    private boolean isPlaybackActivated;
    private boolean isQueueClosed;
    private boolean isTerminated;

    public Partyroom() {}

    @Builder
    public Partyroom(PartyroomId partyroomId,  StageType stageType, String title, String introduction,
                     String linkDomain, int playbackTimeLimit,
                     UserId hostId, String noticeContent,
                     List<Partymember> partymembers, List<Dj> djs,
                     PlaylistId currentPlaybackId,
                     boolean isPlaybackActivated, boolean isQueueClosed, boolean isTerminated) {
        this.partyroomId = partyroomId;
        this.stageType = stageType;
        this.title = title;
        this.introduction = introduction;
        this.linkDomain = linkDomain;
        this.playbackTimeLimit = playbackTimeLimit;
        this.partymembers = new ArrayList<>();
        this.hostId = hostId;
        this.noticeContent = noticeContent;
        this.currentPlaybackId = currentPlaybackId;
        this.isPlaybackActivated = isPlaybackActivated;
        this.isQueueClosed = isQueueClosed;
        this.isTerminated = isTerminated;
    }

    public static Partyroom create(StageType stageType, String title, String introduction,
                                   String linkDomain, int playbackTimeLimit, UserId hostId) {
        return Partyroom.builder()
                .stageType(stageType)
                .title(title)
                .introduction(introduction)
                .linkDomain(linkDomain)
                .playbackTimeLimit(playbackTimeLimit)
                .partymembers(new ArrayList<>())
                .hostId(hostId)
                .noticeContent("This is notice")
                .currentPlaybackId(null)
                .isPlaybackActivated(false)
                .isQueueClosed(false)
                .isTerminated(false)
                .build();
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