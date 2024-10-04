package com.pfplaybackend.api.partyroom.domain.entity.domainmodel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.partyroom.domain.entity.data.CrewData;
import com.pfplaybackend.api.partyroom.domain.entity.data.DjData;
import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import com.pfplaybackend.api.partyroom.domain.enums.StageType;
import com.pfplaybackend.api.partyroom.domain.value.*;
import com.pfplaybackend.api.partyroom.presentation.payload.request.management.CreatePartyroomRequest;
import com.pfplaybackend.api.partyroom.presentation.payload.request.management.UpdatePartyroomRequest;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    private Set<Crew> crewSet;
    private Set<Dj> djSet;
    private PlaybackId currentPlaybackId;
    private boolean isPlaybackActivated;
    private boolean isQueueClosed;
    private boolean isTerminated;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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
        this.crewSet = new HashSet<>();
        this.djSet = new HashSet<>();
        this.noticeContent = "";
        this.currentPlaybackId = null;
        this.isPlaybackActivated = false;
        this.isQueueClosed = false;
        this.isTerminated = false;
    }

    @Builder
    public Partyroom(PartyroomId partyroomId, StageType stageType, String title, String introduction,
                     String linkDomain, int playbackTimeLimit,
                     UserId hostId, String noticeContent, PlaybackId currentPlaybackId,
                     boolean isPlaybackActivated, boolean isQueueClosed, boolean isTerminated,
                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.partyroomId = partyroomId;
        this.title = title;
        this.introduction = introduction;
        this.linkDomain = linkDomain;
        this.playbackTimeLimit = playbackTimeLimit;
        this.crewSet = new HashSet<>();
        this.djSet = new HashSet<>();
        this.stageType = stageType;
        this.hostId = hostId;
        this.noticeContent = noticeContent;
        this.currentPlaybackId = currentPlaybackId;
        this.isPlaybackActivated = isPlaybackActivated;
        this.isQueueClosed = isQueueClosed;
        this.isTerminated = isTerminated;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Partyroom create(CreatePartyroomRequest request, StageType stageType, UserId hostId) {
        return new Partyroom(stageType, hostId, request.getTitle(), request.getIntroduction(),
                request.getLinkDomain(), request.getPlaybackTimeLimit());
    }

    public boolean isExceededLimit() {
        return this.getCrewSet().size() > 49;
    }

    public Partyroom assignCrewSet(Set<Crew> crewSet) {
        this.crewSet = crewSet;
        return this;
    }

    public Partyroom assignDjSet(Set<Dj> djSet) {
        this.djSet = djSet;
        return this;
    }

    public Optional<Crew> getCrewByUserId(UserId userId) {
        return this.crewSet.stream().filter(crew -> crew.getUserId().equals(userId)).findFirst();
    }

    public Partyroom addNewCrew(UserId userId, AuthorityTier authorityTier, GradeType gradeType) {
        this.crewSet.add(Crew.create(userId, this.partyroomId, authorityTier, gradeType));
        return this;
    }

    public Partyroom createAndAddDj(PlaylistId playlistId, UserId userId) {
        Crew crewIdOfDj = this.getCrewByUserId(userId).orElseThrow();
        CrewId crewId = new CrewId(crewIdOfDj.getId());
        // TODO Dj 객체는 'Dj 신청 레코드'와 연관되어야 하며, 기본적으로는 'Dj 역할'의 크루를 지칭하는 개념으로 존재해야한다.
        this.djSet.add(Dj.create(partyroomId, playlistId, userId, crewId,this.djSet.size() + 1));
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

    public Partyroom applyDeactivation() {
        this.isPlaybackActivated = false;
        this.currentPlaybackId = null;
        return this;
    }

    public Partyroom rotateDjs() {
        int totalElements = this.djSet.size();
        this.djSet.stream().peek(dj -> {
            if(dj.getOrderNumber() == 1) {
                dj.updateOrderNumber(totalElements);
            }else {
                dj.updateOrderNumber(dj.getOrderNumber() - 1);
            }
        });
        return this;
    }

    public Crew deactivateCrewAndGet(UserId userId) {
        this.crewSet = this.crewSet.stream().peek(crew -> {
                    if(crew.getUserId().equals(userId)) {
                        crew.applyDeactivation();
                    }
                }).collect(Collectors.toSet());
        return this.crewSet.stream().filter(crew -> crew.getUserId().equals(userId)).findAny().orElseThrow();
    }

    public void tryRemoveInDjQueue(UserId userId) {
        this.djSet = this.djSet.stream().peek(dj -> {
            if(dj.getUserId().equals(userId)) {
                dj.applyDeleted();
            }
        }).collect(Collectors.toSet());
    }


    public boolean isUserInactiveCrew(UserId userId) {
        return this.crewSet.stream().anyMatch(crew -> crew.getUserId().equals(userId) && !crew.isActive());
    }

    public boolean isUserBannedCrew(UserId userId) {
        return this.crewSet.stream().filter(crew -> crew.getUserId().equals(userId)).findAny().orElseThrow().isBanned();
    }

    public Partyroom activateCrew(UserId userId) {
        this.crewSet = this.crewSet.stream().peek(crew -> {
            if (crew.getUserId().equals(userId)) {
                crew.applyActivation();
            }
        }).collect(Collectors.toSet());
        return this;
    }

    public Partyroom updateCrewGrade(CrewId crewId, GradeType gradeType) {
        this.crewSet = this.crewSet.stream().peek(crew -> {
            if (crew.getId() == (crewId.getId())) {
                crew.updateGrade(gradeType);
            }
        }).collect(Collectors.toSet());
        return this;
    }

    public Crew getCrew(CrewId crewId) {
        return this.crewSet.stream().filter(partymember -> partymember.getId() == crewId.getId()).findAny().orElseThrow();
    }

    public Partyroom updateBaseInfo(UpdatePartyroomRequest request) {
        this.title = request.getTitle();
        this.introduction = request.getIntroduction();
        this.linkDomain = request.getLinkDomain();
        this.playbackTimeLimit = request.getPlaybackTimeLimit();
        return this;
    }
}