package com.pfplaybackend.api.partyroom.domain.entity.data;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import com.pfplaybackend.api.user.domain.value.UserId;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
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

    private boolean isActive;

    private GradeType gradeType;

    private boolean isBanned;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthorityTier authorityTier;


    // 데이터 엔티티 생성자
    public PartymemberData() {}
    public PartymemberData(UserId userId, AuthorityTier authorityTier) {
        this.userId = userId;
        this.authorityTier = authorityTier;
    }
}