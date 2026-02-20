package com.pfplaybackend.api.party.domain.entity.data;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PlaylistId;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.Objects;

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
            @AttributeOverride(name = "id", column = @Column(name = "crew_id")),
    })
    private CrewId crewId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "playlist_id")),
    })
    private PlaylistId playlistId;

    private int orderNumber;

    // 데이터 엔티티 생성자
    public DjData() {
    }

    @Builder
    public DjData(Long id, CrewId crewId, PlaylistId playlistId, int orderNumber,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.crewId = crewId;
        this.playlistId = playlistId;
        this.orderNumber = orderNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public DjData assignPartyroomData(PartyroomData partyroomData) {
        this.partyroomData = partyroomData;
        return this;
    }

    // ── Business Methods ──

    public static DjData create(PartyroomData partyroomData, PlaylistId playlistId, CrewId crewId, int orderNumber) {
        DjData dj = DjData.builder()
                .playlistId(playlistId)
                .crewId(crewId)
                .orderNumber(orderNumber)
                .build();
        dj.assignPartyroomData(partyroomData);
        return dj;
    }

    public void updateOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DjData djData = (DjData) o;
        return Objects.equals(id, djData.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}