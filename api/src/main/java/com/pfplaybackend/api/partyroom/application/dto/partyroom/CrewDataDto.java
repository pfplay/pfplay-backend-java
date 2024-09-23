package com.pfplaybackend.api.partyroom.application.dto.partyroom;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.partyroom.domain.entity.data.CrewData;
import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public static CrewDataDto from(CrewData crewData) {
        return new CrewDataDto(
                crewData.getId(),
                crewData.getPartyroomData().getPartyroomId(),
                crewData.getUserId(),
                crewData.getAuthorityTier(),
                crewData.getGradeType(),
                crewData.isActive()
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