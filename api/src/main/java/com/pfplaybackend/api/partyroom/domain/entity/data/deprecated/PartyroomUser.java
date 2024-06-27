package com.pfplaybackend.api.partyroom.domain.entity.data.deprecated;

import com.pfplaybackend.api.partyroom.domain.enums.deprecated.PartyroomGrade;
import com.pfplaybackend.api.user.domain.value.UserId;
import jakarta.persistence.*;
import lombok.*;

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
