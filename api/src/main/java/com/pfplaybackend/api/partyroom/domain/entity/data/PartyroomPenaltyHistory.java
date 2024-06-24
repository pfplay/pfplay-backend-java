package com.pfplaybackend.api.partyroom.domain.entity.data;

import com.pfplaybackend.api.partyroom.domain.enums.PartyroomPenaltyType;
import com.pfplaybackend.api.user.domain.value.UserId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Builder
@AllArgsConstructor
public class PartyroomPenaltyHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "integer unsigned")
    private Long id;

    @Embedded
    private UserId userId;
    private String partyroomId;

    @Enumerated(EnumType.STRING)
    private PartyroomPenaltyType partyroomPenaltyType;

    public PartyroomPenaltyHistory() {}
}
