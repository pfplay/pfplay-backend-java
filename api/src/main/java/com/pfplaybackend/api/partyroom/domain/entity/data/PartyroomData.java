package com.pfplaybackend.api.partyroom.domain.entity.data;

import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.domain.enums.StageType;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.domain.value.PlaybackId;
import com.pfplaybackend.api.user.domain.value.UserId;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

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
public class PartyroomData {

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
    
    @OneToMany(mappedBy = "partyroomData", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartymemberData> partymemberDataList = new ArrayList<>();

    @OneToMany(mappedBy = "partyroomData", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DjData> djDataList = new ArrayList<>();

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
                         String noticeContent, PlaybackId currentPlaybackId, boolean isPlaybackActivated, boolean isQueueClosed, boolean isTerminated) {
        this.id = id;
        this.partyroomId = partyroomId;
        this.hostId = hostId;
        this.stageType = stageType;
        this.title = title;
        this.introduction = introduction;
        this.linkDomain = linkDomain;
        this.playbackTimeLimit = playbackTimeLimit;
        // Assign Empty Collection
        this.partymemberDataList = new ArrayList<>();
        this.djDataList = new ArrayList<>();
        //
        this.noticeContent = noticeContent;
        this.currentPlaybackId = currentPlaybackId;
        this.isPlaybackActivated = isPlaybackActivated;
        this.isQueueClosed = isQueueClosed;
        this.isTerminated = isTerminated;
    }

    public PartyroomData assignPartymemberListData(List<PartymemberData> partymemberDataList) {
        this.partymemberDataList.clear();
        this.partymemberDataList.addAll(partymemberDataList);
        return this;
    }

    public PartyroomData assignDjListData(List<DjData> djDataList) {
        this.djDataList.clear();
        this.djDataList.addAll(djDataList);
        return this;
    }

    public PartyroomData applyActivation() {
        this.isPlaybackActivated = true;
        return this;
    }
}