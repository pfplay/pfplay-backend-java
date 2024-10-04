package com.pfplaybackend.api.partyroom.domain.entity.data;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.partyroom.application.dto.base.PartyroomDataDto;
import com.pfplaybackend.api.partyroom.application.dto.partyroom.PartyroomDto;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Crew;
import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import com.pfplaybackend.api.partyroom.domain.enums.StageType;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.domain.value.PlaybackId;
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

import static com.pfplaybackend.api.partyroom.domain.entity.data.QCrewData.crewData;

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


    public static PartyroomData from(PartyroomDataDto dto) {
        return PartyroomData.builder()
                .id(dto.getId())
                .partyroomId(dto.getPartyroomId())
                .hostId(dto.getHostId())
                .stageType(dto.getStageType())
                .title(dto.getTitle())
                .introduction(dto.getIntroduction())
                .linkDomain(dto.getLinkDomain())
                .playbackTimeLimit(dto.getPlaybackTimeLimit())
                .noticeContent(dto.getNoticeContent())
                .currentPlaybackId(dto.getCurrentPlaybackId())
                .isPlaybackActivated(dto.isPlaybackActivated())
                .isQueueClosed(dto.isQueueClosed())
                .isTerminated(dto.isTerminated())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }
}