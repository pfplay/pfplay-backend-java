package com.pfplaybackend.api.partyroom.domain.entity.data.history;

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
        name = "USER_BLOCK_HISTORY"
)
@Entity
public class UserBlockHistoryData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "uid", column = @Column(name = "blocker_user_id")),
    })
    private UserId blockerUserId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "uid", column = @Column(name = "blocked_user_id")),
    })
    private UserId blockedUserId;

    @Column(name = "block_reason", length = 255)
    private String blockReason;

    @Column(name = "block_date", nullable = false)
    private LocalDateTime blockDate;

    @Column(name = "is_unblocked", nullable = false)
    private boolean isUnblocked = false;

    @Column(name = "unblock_date")
    private LocalDateTime unblockDate;

    public UserBlockHistoryData() {}
}
