package com.pfplaybackend.api.partyroom.presentation.payload.response;

import com.pfplaybackend.api.partyroom.application.dto.CurrentDjWithProfileDto;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Crew;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryPartyroomSummaryResponse {
    String title;
    String introduction;
    CurrentDjWithProfileDto currentDj;

    public static QueryPartyroomSummaryResponse from(Partyroom partyroom, Crew crew, ProfileSettingDto profileSettingDto) {
        return new QueryPartyroomSummaryResponse(partyroom.getTitle(), partyroom.getIntroduction(),
                new CurrentDjWithProfileDto(crew.getId(), profileSettingDto.getNickname(), profileSettingDto.getAvatarIconUri()));
    }
}
