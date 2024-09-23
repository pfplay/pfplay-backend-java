package com.pfplaybackend.api.partyroom.domain.entity.domainmodel;

import com.google.common.collect.ImmutableList;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import com.pfplaybackend.api.partyroom.domain.enums.StageType;
import com.pfplaybackend.api.partyroom.domain.value.CrewId;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.domain.value.PlaybackId;
import com.pfplaybackend.api.partyroom.domain.value.PlaylistId;
import com.pfplaybackend.api.partyroom.presentation.payload.request.management.CreatePartyroomRequest;
import com.pfplaybackend.api.partyroom.presentation.payload.request.management.UpdatePartyroomRequest;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private List<Crew> crews;
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
        this.crews = new ArrayList<>();
        this.djs = new ArrayList<>();
        this.noticeContent = "";
        this.currentPlaybackId = null;
        this.isPlaybackActivated = false;
        this.isQueueClosed = false;
        this.isTerminated = false;
    }

    @Builder
    public Partyroom(PartyroomId partyroomId, StageType stageType, String title, String introduction,
                     String linkDomain, int playbackTimeLimit,
                     UserId hostId, String noticeContent,
                     List<Crew> crews, List<Dj> djs,
                     PlaybackId currentPlaybackId,
                     boolean isPlaybackActivated, boolean isQueueClosed, boolean isTerminated) {
        this.partyroomId = partyroomId;
        this.title = title;
        this.introduction = introduction;
        this.linkDomain = linkDomain;
        this.playbackTimeLimit = playbackTimeLimit;
        this.crews = new ArrayList<>();
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

    public boolean isExceededLimit() {
        return this.getCrews().size() > 49;
    }

    public Partyroom assignCrews(List<Crew> crews) {
        this.crews = crews;
        return this;
    }

    public Partyroom assignDjs(List<Dj> djs) {
        this.djs = djs;
        return this;
    }

    public Optional<Crew> getCrewByUserId(UserId userId) {
        return this.crews.stream().filter(crew -> crew.getUserId().equals(userId)).findFirst();
    }

    public Partyroom addNewCrew(UserId userId, AuthorityTier authorityTier, GradeType gradeType) {
        this.crews = new ImmutableList.Builder<Crew>()
                .addAll(this.crews)
                .add(Crew.create(userId, this.partyroomId, authorityTier, gradeType))
                .build();
        return this;
    }

    public Partyroom createAndAddDj(PlaylistId playlistId, UserId userId) {
        // Dj 객체는 'Dj 신청 레코드'와 연관되어야 하며, 기본적으로는 'Dj 역할'의 크루를 지칭해야 한다.
        this.djs = new ImmutableList.Builder<Dj>()
                .addAll(this.djs)
                .add(Dj.create(partyroomId, playlistId, userId, this.djs.size() + 1))
                .build();
        return this;
    }

    public Partyroom updatePlaybackId(PlaybackId playbackId) {
        this.currentPlaybackId = playbackId;
        return this;
    }

    public Partyroom applyActivation() {
        this.isPlaybackActivated = true;
        return this;
    }

    public Partyroom rotateDjs() {
        int totalElements = this.djs.size();
        this.djs = new ImmutableList.Builder<Dj>()
                .addAll(this.djs)
                .build().stream().peek(dj -> {
                    if(dj.getOrderNumber() == 1) {
                        dj.updateOrderNumber(totalElements);
                    }else {
                        dj.updateOrderNumber(dj.getOrderNumber() - 1);
                    }
                }).toList();
        return this;
    }

    public Crew deactivateCrewAndGet(UserId userId) {
        this.crews = new ImmutableList.Builder<Crew>()
                .addAll(this.crews)
                .build().stream().map(crew -> {
                    if(crew.getUserId().equals(userId)) {
                        return crew.applyDeactivation();
                    }else {
                        return crew;
                    }
                }).toList();
        return this.crews.stream().filter(crew -> crew.getUserId().equals(userId)).findAny().orElseThrow();
    }

    public boolean isUserInactiveCrew(UserId userId) {
        return this.crews.stream().anyMatch(crew -> crew.getUserId().equals(userId) && !crew.isActive());
    }

    public boolean isUserBannedCrew(UserId userId) {
        return this.crews.stream().filter(crew -> crew.getUserId().equals(userId)).findAny().orElseThrow().isBanned();
    }

    public Partyroom activateCrew(UserId userId) {
        this.crews = new ImmutableList.Builder<Crew>()
                .addAll(this.crews.stream()
                        .peek(crew -> {
                            if (crew.getUserId().equals(userId)) {
                                crew.applyActivation();
                            }
                        }).toList()
                ).build();
        return this;
    }

    public Partyroom updateCrewGrade(CrewId crewId, GradeType gradeType) {
        this.crews = new ImmutableList.Builder<Crew>()
                .addAll(this.crews)
                .build()
                .stream().map(crew -> {
                    if(crew.getId() == (crewId.getId())) {
                        return crew.updateGrade(gradeType);
                    }else {
                        return crew;
                    }
                }).toList();
        return this;
    }

    public Crew getCrew(CrewId crewId) {
        return this.crews.stream().filter(partymember -> partymember.getId() == crewId.getId()).findAny().orElseThrow();
    }

    public Partyroom updateBaseInfo(UpdatePartyroomRequest request) {
        this.title = request.getTitle();
        this.introduction = request.getIntroduction();
        this.linkDomain = request.getLinkDomain();
        this.playbackTimeLimit = request.getPlaybackTimeLimit();
        return this;
    }
}