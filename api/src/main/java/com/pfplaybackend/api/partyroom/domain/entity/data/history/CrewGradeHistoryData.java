package com.pfplaybackend.api.partyroom.domain.entity.data.history;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import com.pfplaybackend.api.partyroom.domain.value.CrewId;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Getter
@DynamicInsert
@DynamicUpdate
@Table(
        name = "CREW_GRADE_HISTORY"
)
@Entity
public class CrewGradeHistoryData extends BaseEntity {
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
            @AttributeOverride(name = "id", column = @Column(name = "adjuster_crew_id")),
    })
    private CrewId adjusterCrewId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "adjusted_crew_id")),
    })
    private CrewId adjustedCrewId;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_grade", length = 50)
    private GradeType previousGradeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_grade", length = 50)
    private GradeType newGradeType;

    @Column(name = "adjustment_date", nullable = false)
    private LocalDateTime adjustmentDate;

    public CrewGradeHistoryData() {}
}
