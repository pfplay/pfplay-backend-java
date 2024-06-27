package com.pfplaybackend.api.partyroom.domain.entity.data;

import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.user.domain.value.UserId;
import jakarta.persistence.*;
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

    // 호스트 유저
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "uid", column = @Column(name = "host_id")),
    })
    private UserId hostId;

    @OneToMany(mappedBy = "partyroomData", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartymemberData> partymembers = new ArrayList<>();

    @OneToMany(mappedBy = "partyroomData", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DjData> djs = new ArrayList<>();

    // 공지사항 내용
    private String noticeContent;

    // 현재 진행중인 재생 식별자
    private long currentPlaybackId;

    // 재생이 활성화 되었는가 여부
    private boolean isPlaybackActivated;

    // 대기열이 닫혔는가 여부
    private boolean isQueueClosed;

    // 운영 종결되었는가
    private boolean isTerminated;

    @PostPersist
    public void updatePartyroomId() {
        initializePartyroomId();
    }

    @PostLoad
    private void postLoad() {
        initializePartyroomId();
    }

    // 식별자 필드를 초기화하는 메서드
    private void initializePartyroomId() {
        this.partyroomId = new PartyroomId(this.id);
    }

    // 데이터 엔티티 생성자
    public PartyroomData() {}

}