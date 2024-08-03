package com.pfplaybackend.api.partyroom.domain.entity.data;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.partyroom.domain.value.PlaylistId;
import com.pfplaybackend.api.user.domain.value.UserId;
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
            @AttributeOverride(name = "id", column = @Column(name = "playlist_id")),
    })
    private PlaylistId playlistId;

    private int orderNumber;

    // Dj 대기열에서 삭제되었을 경우, 레코드 무효화
    private boolean isDeleted;

    // 데이터 엔티티 생성자
    public DjData() {}

    @Builder
    public DjData(Long id, UserId userId, PartyroomData partyroomData, PlaylistId playlistId, int orderNumber, boolean isDeleted) {
        this.id = id;
        this.userId = userId;
        this.playlistId = playlistId;
        this.orderNumber = orderNumber;
        this.isDeleted = isDeleted;
    }

    public DjData assignPartyroomData(PartyroomData partyroomData) {
        this.partyroomData = partyroomData;
        return this;
    }
}