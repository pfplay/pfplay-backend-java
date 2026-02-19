package com.pfplaybackend.api.party.domain.entity.data;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.party.domain.enums.QueueStatus;
import com.pfplaybackend.api.party.domain.enums.StageType;
import com.pfplaybackend.api.party.domain.value.*;
import com.pfplaybackend.api.party.interfaces.api.rest.payload.request.management.CreatePartyroomRequest;
import com.pfplaybackend.api.party.interfaces.api.rest.payload.request.management.UpdateDjQueueStatusRequest;
import com.pfplaybackend.api.party.interfaces.api.rest.payload.request.management.UpdatePartyroomRequest;
import com.pfplaybackend.api.user.domain.value.UserId;
import jakarta.persistence.*;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;

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
        this.noticeContent = noticeContent;
        this.currentPlaybackId = currentPlaybackId;
        this.isPlaybackActivated = isPlaybackActivated;
        this.isQueueClosed = isQueueClosed;
        this.isTerminated = isTerminated;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    // ── Business Methods ──

    public PartyroomData updatePlaybackId(PlaybackId playbackId) {
        this.currentPlaybackId = playbackId;
        return this;
    }

    public PartyroomData applyDeactivation() {
        this.isPlaybackActivated = false;
        this.currentPlaybackId = null;
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
