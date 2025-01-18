package com.pfplaybackend.api.party.domain.entity.data.history;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.user.domain.value.UserId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Setter
@Getter
@DynamicInsert
@DynamicUpdate
@Table(
        name = "CREW_BLOCK_HISTORY"
)
@Entity
public class CrewBlockHistoryData extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "blocker_crew_id")),
    })
    private CrewId blockerCrewId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "blocked_crew_id")),
    })
    private CrewId blockedCrewId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "uid", column = @Column(name = "blocked_user_id")),
    })
    private UserId blockedUserId;

    @Column(name = "block_date", nullable = false)
    private LocalDateTime blockDate;

    @Column(name = "is_unblocked", nullable = false)
    private boolean unblocked = false;

    @Column(name = "unblock_date")
    private LocalDateTime unblockDate;

    public CrewBlockHistoryData() {}
}
