package com.pfplaybackend.api.partyroom.model.entity;

import com.pfplaybackend.api.partyroom.enums.PartyroomGrade;
import com.pfplaybackend.api.partyroom.repository.PartyroomRepository;
import com.pfplaybackend.api.partyroom.repository.PartyroomUserRepository;
import com.pfplaybackend.api.user.domain.model.value.UserId;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Builder
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class PartyroomUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "integer unsigned")
    private Long id;

    @Embedded
    private UserId userId;
    private String nickname;
    private String partyroomId;

    @Enumerated(EnumType.STRING)
    private PartyroomGrade partyroomGrade;
}
