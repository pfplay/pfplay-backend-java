package com.pfplaybackend.api.party.domain.entity.data;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.enums.QueueStatus;
import com.pfplaybackend.api.party.domain.enums.StageType;
import com.pfplaybackend.api.party.domain.exception.CrewException;
import com.pfplaybackend.api.party.domain.exception.DjException;
import com.pfplaybackend.api.party.domain.value.*;
import com.pfplaybackend.api.party.interfaces.api.rest.payload.request.management.CreatePartyroomRequest;
import com.pfplaybackend.api.party.interfaces.api.rest.payload.request.management.UpdateDjQueueStatusRequest;
import com.pfplaybackend.api.party.interfaces.api.rest.payload.request.management.UpdatePartyroomRequest;
import com.pfplaybackend.api.user.domain.value.UserId;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Getter
@DynamicInsert
@DynamicUpdate
@Table(
        name = "PARTYROOM",
        indexes = {
                @Index(name = "paytyroom_host_id_IDX", columnList = "host_id")
        }
)
@Entity
public class PartyroomData extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partyroom_id")
    private Long id;

    @Transient
    private PartyroomId partyroomId;

    @Enumerated(EnumType.STRING)
    private StageType stageType;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "uid", column = @Column(name = "host_id")),
    })
    private UserId hostId;

    // 파티룸 타이틀
    private String title;
    // 파티룸 소개글
    private String introduction;
    // 링크 도메인
    private String linkDomain;
    // 재생 길이 제약
    private int playbackTimeLimit;
    
    @OneToMany(mappedBy = "partyroomData", cascade = CascadeType.ALL)
    private Set<CrewData> crewDataSet;

    @OneToMany(mappedBy = "partyroomData", cascade = CascadeType.ALL)
    private Set<DjData> djDataSet;

    // 공지사항 내용
    private String noticeContent;
    // 현재 진행중인 재생 식별자
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "current_playback_id")),
    })
    private PlaybackId currentPlaybackId;
    // 재생이 활성화 되었는가 여부
    private boolean isPlaybackActivated;
    // 대기열이 닫혔는가 여부
    private boolean isQueueClosed;
    // 폐쇄되었는가 여부
    private boolean isTerminated;

    @PostPersist
    public void updatePartyroomId() {
        initializePartyroomId();
    }

    @PostLoad
    private void postLoad() {
        initializePartyroomId();
    }

    // Initialize identifier value object(=PartyroomId)
    private void initializePartyroomId() {
        this.partyroomId = new PartyroomId(this.id);
    }

    // 데이터 엔티티 생성자
    public PartyroomData() {}

    @Builder
    public PartyroomData(Long id, PartyroomId partyroomId, UserId hostId, StageType stageType,
                         String title, String introduction, String linkDomain, int playbackTimeLimit,
                         String noticeContent, PlaybackId currentPlaybackId, boolean isPlaybackActivated, boolean isQueueClosed, boolean isTerminated,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.partyroomId = partyroomId;
        this.hostId = hostId;
        this.stageType = stageType;
        this.title = title;
        this.introduction = introduction;
        this.linkDomain = linkDomain;
        this.playbackTimeLimit = playbackTimeLimit;
        // Assign Empty Collection
        this.crewDataSet = new HashSet<>();
        this.djDataSet = new HashSet<>();
        //
        this.noticeContent = noticeContent;
        this.currentPlaybackId = currentPlaybackId;
        this.isPlaybackActivated = isPlaybackActivated;
        this.isQueueClosed = isQueueClosed;
        this.isTerminated = isTerminated;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public PartyroomData assignCrewDataSet(Set<CrewData> crewDataSet) {
        this.crewDataSet = crewDataSet;
        return this;
    }

    public PartyroomData assignDjDataSet(Set<DjData> djDataSet) {
        this.djDataSet = djDataSet;
        return this;
    }

    public PartyroomData applyActivation() {
        this.isPlaybackActivated = true;
        return this;
    }


    // ── Factory Method ──

    public static PartyroomData create(CreatePartyroomRequest request, StageType stageType, UserId hostId) {
        return PartyroomData.builder()
                .stageType(stageType)
                .hostId(hostId)
                .title(request.getTitle())
                .introduction(request.getIntroduction())
                .linkDomain(request.getLinkDomain())
                .playbackTimeLimit(request.getPlaybackTimeLimit())
                .noticeContent("")
                .isPlaybackActivated(false)
                .isQueueClosed(false)
                .isTerminated(false)
                .build();
    }

    // ── Convenience Accessors ──

    public Set<CrewData> getActiveCrewDataSet() {
        return this.crewDataSet.stream()
                .filter(CrewData::isActive)
                .collect(Collectors.toSet());
    }

    public Set<DjData> getQueuedDjDataSet() {
        return this.djDataSet.stream()
                .filter(DjData::isQueued)
                .collect(Collectors.toSet());
    }

    // ── Crew Business Methods ──

    public boolean isExceededLimit() {
        return this.getActiveCrewDataSet().size() > 49;
    }

    public Optional<CrewData> getCrewByUserId(UserId userId) {
        return this.crewDataSet.stream()
                .filter(crew -> crew.getUserId().equals(userId))
                .findFirst();
    }

    public PartyroomData addNewCrew(UserId userId, AuthorityTier authorityTier, GradeType gradeType) {
        CrewData crew = CrewData.create(this, userId, authorityTier, gradeType);
        this.crewDataSet.add(crew);
        return this;
    }

    public PartyroomData activateCrew(UserId userId) {
        this.crewDataSet.stream()
                .filter(crew -> crew.getUserId().equals(userId))
                .forEach(CrewData::activatePresence);
        return this;
    }

    public CrewData deactivateCrewAndGet(UserId userId) {
        this.crewDataSet.stream()
                .filter(crew -> crew.getUserId().equals(userId))
                .forEach(CrewData::deactivatePresence);
        return this.crewDataSet.stream()
                .filter(crew -> crew.getUserId().equals(userId))
                .findAny().orElseThrow();
    }

    public boolean isUserInactiveCrew(UserId userId) {
        return this.crewDataSet.stream()
                .anyMatch(crew -> crew.getUserId().equals(userId) && !crew.isActive());
    }

    public boolean isUserBannedCrew(UserId userId) {
        return this.crewDataSet.stream()
                .filter(crew -> crew.getUserId().equals(userId))
                .findAny().orElseThrow()
                .isBanned();
    }

    public CrewData getCrew(CrewId crewId) {
        return this.crewDataSet.stream()
                .filter(crew -> crew.getId().equals(crewId.getId()))
                .findAny()
                .orElseThrow(() -> ExceptionCreator.create(CrewException.NOT_FOUND_ACTIVE_ROOM));
    }

    public void updateCrewGrade(CrewId crewId, GradeType gradeType) {
        this.crewDataSet.stream()
                .filter(crew -> crew.getId().equals(crewId.getId()))
                .forEach(crew -> crew.updateGrade(gradeType));
    }

    public void applyPermanentBan(CrewId crewId) {
        this.crewDataSet.stream()
                .filter(crew -> crew.getId().equals(crewId.getId()))
                .forEach(CrewData::enforceBan);
    }

    public void removePermanentBan(CrewId crewId) {
        this.crewDataSet.stream()
                .filter(crew -> crew.getId().equals(crewId.getId()))
                .forEach(CrewData::releaseBan);
    }

    // ── DJ Business Methods ──

    public PartyroomData createAndAddDj(PlaylistId playlistId, UserId userId) {
        CrewData crewOfDj = this.getCrewByUserId(userId).orElseThrow();
        if (this.djDataSet.stream().anyMatch(dj -> dj.getUserId().equals(userId) && dj.isQueued())) {
            throw ExceptionCreator.create(DjException.ALREADY_REGISTERED);
        }
        CrewId crewId = new CrewId(crewOfDj.getId());
        this.djDataSet.add(DjData.create(this, playlistId, userId, crewId, this.djDataSet.size() + 1));
        return this;
    }

    public PartyroomData rotateDjs() {
        int totalElements = this.djDataSet.size();
        this.djDataSet.forEach(dj -> {
            if (dj.getOrderNumber() == 1) {
                dj.updateOrderNumber(totalElements);
            } else {
                dj.updateOrderNumber(dj.getOrderNumber() - 1);
            }
        });
        return this;
    }

    public void tryRemoveInDjQueue(CrewId crewId) {
        AtomicInteger orderNumber = new AtomicInteger(1);
        this.djDataSet.stream()
                .sorted(Comparator.comparingInt(DjData::getOrderNumber))
                .forEach(dj -> {
                    if (dj.getCrewId().equals(crewId)) {
                        dj.applyDequeued();
                    } else {
                        dj.updateOrderNumber(orderNumber.getAndIncrement());
                    }
                });
    }

    public Optional<DjData> getDjById(Long djId) {
        return this.djDataSet.stream()
                .filter(dj -> dj.getId() != null && dj.getId().equals(djId))
                .findFirst();
    }

    public Optional<DjData> getCurrentDj() {
        return this.djDataSet.stream()
                .filter(dj -> dj.getOrderNumber() == 1)
                .findFirst();
    }

    public boolean isCurrentDj(CrewId crewId) {
        if (isPlaybackActivated) {
            return this.djDataSet.stream()
                    .anyMatch(dj -> dj.getCrewId().equals(crewId) && dj.getOrderNumber() == 1);
        }
        return false;
    }

    // ── Other Business Methods ──

    public PartyroomData updatePlaybackId(PlaybackId playbackId) {
        this.currentPlaybackId = playbackId;
        return this;
    }

    public PartyroomData applyDeactivation() {
        this.isPlaybackActivated = false;
        this.currentPlaybackId = null;
        this.djDataSet.stream().filter(DjData::isQueued).forEach(DjData::applyDequeued);
        return this;
    }

    public PartyroomData updateBaseInfo(UpdatePartyroomRequest request) {
        this.title = request.getTitle();
        this.introduction = request.getIntroduction();
        this.linkDomain = request.getLinkDomain();
        this.playbackTimeLimit = request.getPlaybackTimeLimit();
        return this;
    }

    public void updatedQueueStatus(UpdateDjQueueStatusRequest request) {
        if (request.getQueueStatus().equals(QueueStatus.CLOSE)) this.isQueueClosed = true;
        if (request.getQueueStatus().equals(QueueStatus.OPEN)) this.isQueueClosed = false;
    }

    public void terminate() {
        this.isTerminated = true;
    }

    public PartyroomData assignPartyroomId(PartyroomId partyroomId) {
        this.partyroomId = partyroomId;
        return this;
    }
}