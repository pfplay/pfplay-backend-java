package com.pfplaybackend.api.party.domain.entity.data.history;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.enums.PenaltyType;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
@Table(
        name = "CREW_PENALTY_HISTORY"
)
@Entity
@AllArgsConstructor
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

    @Enumerated(EnumType.STRING)
    private PenaltyType penaltyType;

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
    private boolean released;

    @Column(name = "release_date")
    private LocalDateTime releaseDate;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "released_by_crew_id")),
    })
    private CrewId releasedByCrewId;

    public CrewPenaltyHistoryData() {}

    public void release(CrewId releaserCrewId) {
        this.released = true;
        this.releasedByCrewId = releaserCrewId;
        this.releaseDate = LocalDateTime.now();
    }
}