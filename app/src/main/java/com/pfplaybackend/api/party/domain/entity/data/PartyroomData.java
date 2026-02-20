package com.pfplaybackend.api.party.domain.entity.data;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.party.domain.enums.StageType;
import com.pfplaybackend.api.party.domain.exception.GradeException;
import com.pfplaybackend.api.party.domain.exception.PartyroomException;
import com.pfplaybackend.api.party.domain.value.LinkDomain;
import com.pfplaybackend.api.party.domain.value.LinkDomainConverter;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.value.PlaybackTimeLimit;
import com.pfplaybackend.api.party.domain.value.PlaybackTimeLimitConverter;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.common.domain.value.UserId;
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
                @Index(name = "partyroom_host_id_IDX", columnList = "host_id")
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
    @Convert(converter = LinkDomainConverter.class)
    private LinkDomain linkDomain;
    // 재생 길이 제약
    @Convert(converter = PlaybackTimeLimitConverter.class)
    private PlaybackTimeLimit playbackTimeLimit;

    // 공지사항 내용
    private String noticeContent;
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
    protected PartyroomData() {}

    @Builder
    public PartyroomData(Long id, PartyroomId partyroomId, UserId hostId, StageType stageType,
                         String title, String introduction, LinkDomain linkDomain, PlaybackTimeLimit playbackTimeLimit,
                         String noticeContent, boolean isTerminated,
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
        this.isTerminated = isTerminated;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ── Factory Method ──

    public static PartyroomData create(String title, String introduction, LinkDomain linkDomain,
                                        PlaybackTimeLimit timeLimit, StageType stageType, UserId hostId) {
        return PartyroomData.builder()
                .stageType(stageType)
                .hostId(hostId)
                .title(title)
                .introduction(introduction)
                .linkDomain(linkDomain)
                .playbackTimeLimit(timeLimit)
                .noticeContent("")
                .isTerminated(false)
                .build();
    }

    // ── Business Methods ──

    public PartyroomData updateBaseInfo(String title, String introduction, LinkDomain linkDomain, PlaybackTimeLimit timeLimit) {
        this.title = title;
        this.introduction = introduction;
        this.linkDomain = linkDomain;
        this.playbackTimeLimit = timeLimit;
        return this;
    }

    // ── Validation Methods ──

    public void validateHost(UserId userId) {
        if (!this.hostId.equals(userId)) {
            throw ExceptionCreator.create(GradeException.GRADE_INSUFFICIENT_FOR_OPERATION);
        }
    }

    public void validateNotTerminated() {
        if (this.isTerminated) {
            throw ExceptionCreator.create(PartyroomException.ALREADY_TERMINATED);
        }
    }

    public void terminate() {
        this.isTerminated = true;
    }

    public PartyroomData assignPartyroomId(PartyroomId partyroomId) {
        this.partyroomId = partyroomId;
        return this;
    }
}
