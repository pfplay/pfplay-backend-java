package com.pfplaybackend.api.partyroom.domain.entity.data;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.partyroom.application.dto.base.CrewDataDto;
import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import com.pfplaybackend.api.user.domain.value.UserId;
import jakarta.persistence.*;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.builder.HashCodeExclude;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;
import java.util.Objects;


@Getter
@DynamicInsert
@DynamicUpdate
@Table(
        name = "CREW",
        indexes = {
                @Index(name = "crew_partroom_id_IDX", columnList = "partyroom_id")
        })
@Entity
public class CrewData extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crew_id")
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
    @Column(name = "is_active")
    private boolean isActive;
    // 파티룸 내에서의 등급
    private GradeType gradeType;
    // 영구 퇴장 페널티 부과 여부
    private boolean isBanned;
    //
    @Column(nullable = false)
    private LocalDateTime enteredAt;
    private LocalDateTime exitedAt;

    // 데이터 엔티티 생성자
    public CrewData() {}
    public CrewData(UserId userId, AuthorityTier authorityTier) {
        this.userId = userId;
        this.authorityTier = authorityTier;
    }

    @Builder
    public CrewData(Long id, UserId userId, AuthorityTier authorityTier, GradeType gradeType,
                    boolean isActive, boolean isBanned, LocalDateTime enteredAt, LocalDateTime exitedAt,
                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.authorityTier = authorityTier;
        this.gradeType = gradeType;
        this.isActive = isActive;
        this.isBanned = isBanned;
        this.enteredAt = enteredAt;
        this.exitedAt = exitedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public CrewData assignPartyroomData(PartyroomData partyroomData) {
        this.partyroomData = partyroomData;
        return this;
    }
}