package com.pfplaybackend.api.partyroom.model.entity;

import com.pfplaybackend.api.partyroom.enums.PartyroomPenaltyType;
import com.pfplaybackend.api.user.model.value.UserId;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Builder
public class PartyroomPenalty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "integer unsigned")
    private Long id;

    @Embedded
    private UserId userId;
    private String partyroomId;

    @Enumerated(EnumType.STRING)
    private PartyroomPenaltyType partyroomPenaltyType;
}
