package com.pfplaybackend.api.partyroom.domain.entity.data.history;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.partyroom.domain.value.CrewId;
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
        name = "CREW_PENALTY_HISTORY"
)
@Entity
public class CrewPenaltyHistoryData extends BaseEntity {
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
            @AttributeOverride(name = "id", column = @Column(name = "punisher_crew_id")),
    })
    private CrewId punisherCrewId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "punished_crew_id")),
    })
    private CrewId punishedCrewId;

    @Column(name = "penalty_reason", length = 255)
    private String penaltyReason;

    @Column(name = "penalty_date", nullable = false)
    private LocalDateTime penaltyDate;

    @Column(name = "is_released", nullable = false)
    private boolean isReleased = false;

    @Column(name = "release_date")
    private LocalDateTime releaseDate;

    @Column(name = "released_by_user_id")
    private Long releasedByUserId;

    public CrewPenaltyHistoryData() {}
}