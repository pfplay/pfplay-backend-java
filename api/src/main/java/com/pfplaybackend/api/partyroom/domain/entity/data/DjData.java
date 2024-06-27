package com.pfplaybackend.api.partyroom.domain.entity.data;

import com.pfplaybackend.api.partyroom.domain.value.PlaylistId;
import jakarta.persistence.*;
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

    // 데이터 엔티티 생성자
    public DjData() {}
}
