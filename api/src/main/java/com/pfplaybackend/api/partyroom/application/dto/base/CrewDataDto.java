package com.pfplaybackend.api.partyroom.application.dto.base;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.partyroom.domain.entity.data.CrewData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Crew;
import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CrewDataDto {
    private Long id;
    private PartyroomId partyroomId;
    private UserId userId;
    private AuthorityTier authorityTier;
    private GradeType gradeType;
    private boolean isActive;
    private boolean isBanned;
    private LocalDateTime enteredAt;
    private LocalDateTime exitedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CrewData toData() {
        return CrewData.builder()
                .id(id)
                .userId(userId)
                .authorityTier(authorityTier)
                .gradeType(gradeType)
                .isActive(isActive)
                .isBanned(isBanned)
                .enteredAt(enteredAt)
                .exitedAt(exitedAt)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    public static CrewDataDto from(CrewData crewData) {
        return new CrewDataDto(
                crewData.getId(),
                crewData.getPartyroomData().getPartyroomId(),
                crewData.getUserId(),
                crewData.getAuthorityTier(),
                crewData.getGradeType(),
                crewData.isActive(),
                crewData.isBanned(),
                crewData.getEnteredAt(),
                crewData.getExitedAt(),
                crewData.getCreatedAt(),
                crewData.getUpdatedAt()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CrewDataDto that = (CrewDataDto) o;
        return Objects.equals(id, that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}