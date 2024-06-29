package com.pfplaybackend.api.partyroom.domain.entity.data;

import com.pfplaybackend.api.partyroom.domain.value.PlaylistId;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Getter
@Table(
        name = "DJ",
        indexes = {
                @Index(name = "dj_partyroom_id_IDX", columnList = "partyroom_id")
        })
@Entity
public class DjData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dj_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partyroom_id")
    private PartyroomData partyroomData;

    @Embedded
    private PlaylistId playlistId;

    private int orderNumber;

    // Dj 대기열에서 삭제되었을 경우, 레코드 무효화
    private boolean isDeleted;

    // 데이터 엔티티 생성자
    public DjData() {}

    @Builder
    public DjData(Long id, PartyroomData partyroomData, PlaylistId playlistId, int orderNumber, boolean isDeleted) {
        this.id = id;
        this.playlistId = playlistId;
        this.orderNumber = orderNumber;
        this.isDeleted = isDeleted;
    }

    public DjData assignPartyroomData(PartyroomData partyroomData) {
        this.partyroomData = partyroomData;
        return this;
    }
}