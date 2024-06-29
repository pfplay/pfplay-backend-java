package com.pfplaybackend.api.partyroom.domain.entity.data;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import com.pfplaybackend.api.user.domain.value.UserId;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Getter
@Table(
        name = "PARTYMEMBER",
        indexes = {
                @Index(name = "paytymember_partroom_id_IDX", columnList = "partyroom_id")
        })
@Entity
public class PartymemberData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partymember_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partyroom_id")
    private PartyroomData partyroomData;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "uid", column = @Column(name = "user_id")),
    })
    private UserId userId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthorityTier authorityTier;

    // 파티룸에서 활동중 여부
    private boolean isActive;
    // 파티룸 내에서의 등급
    private GradeType gradeType;
    // 영구 퇴장 페널티 부과 여부
    private boolean isBanned;

    // 데이터 엔티티 생성자
    public PartymemberData() {}
    public PartymemberData(UserId userId, AuthorityTier authorityTier) {
        this.userId = userId;
        this.authorityTier = authorityTier;
    }

    @Builder
    public PartymemberData(Long id, UserId userId, AuthorityTier authorityTier, GradeType gradeType,
                           boolean isActive, boolean isBanned) {
        this.id = id;
        this.userId = userId;
        this.authorityTier = authorityTier;
        this.gradeType = gradeType;
        this.isActive = isActive;
        this.isBanned = isBanned;
    }

    public PartymemberData assignPartyroomData(PartyroomData partyroomData) {
        this.partyroomData = partyroomData;
        return this;
    }
}