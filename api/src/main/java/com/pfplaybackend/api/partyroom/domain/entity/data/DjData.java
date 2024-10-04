package com.pfplaybackend.api.partyroom.domain.entity.data;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.partyroom.domain.value.CrewId;
import com.pfplaybackend.api.partyroom.domain.value.PlaylistId;
import com.pfplaybackend.api.user.domain.value.UserId;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Getter
@DynamicInsert
@DynamicUpdate
@Table(
        name = "DJ",
        indexes = {
                @Index(name = "dj_partyroom_id_IDX", columnList = "partyroom_id")
        })
@Entity
public class DjData extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dj_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partyroom_id")
    private PartyroomData partyroomData;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "uid", column = @Column(name = "user_id")),
    })
    private UserId userId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "crew_id")),
    })
    private CrewId crewId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "playlist_id")),
    })
    private PlaylistId playlistId;

    private int orderNumber;

    // Dj 대기열에서 삭제되었을 경우, 레코드 무효화
    private boolean isDeleted;

    // 데이터 엔티티 생성자
    public DjData() {
    }

    @Builder
    public DjData(Long id, CrewId crewId, UserId userId, PlaylistId playlistId, int orderNumber, boolean isDeleted,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.crewId = crewId;
        this.userId = userId;
        this.playlistId = playlistId;
        this.orderNumber = orderNumber;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public DjData assignPartyroomData(PartyroomData partyroomData) {
        this.partyroomData = partyroomData;
        return this;
    }
}