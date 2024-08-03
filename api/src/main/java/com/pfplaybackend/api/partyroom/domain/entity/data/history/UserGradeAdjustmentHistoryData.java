package com.pfplaybackend.api.partyroom.domain.entity.data.history;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.user.domain.value.UserId;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Getter
@DynamicInsert
@DynamicUpdate
@Table(
        name = "USER_GRADE_ADJUSTMENT_HISTORY"
)
@Entity
public class UserGradeAdjustmentHistoryData extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "partyroom_id")),
    })
    private PartyroomId partyroomId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "uid", column = @Column(name = "adjusting_user_id")),
    })
    private UserId adjustingUserId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "uid", column = @Column(name = "adjusted_user_id")),
    })
    private UserId adjustedUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_grade", length = 50)
    private GradeType previousGradeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_grade", length = 50)
    private GradeType newGradeType;

    @Column(name = "adjustment_reason", length = 255)
    private String adjustmentReason;

    @Column(name = "adjustment_date", nullable = false)
    private LocalDateTime adjustmentDate;

    public UserGradeAdjustmentHistoryData() {}
}
