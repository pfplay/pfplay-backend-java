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
    private String title;
    private String introduction;
    private String linkDomain;
    private int playbackTimeLimit;
    private CurrentDjWithProfileDto currentDj;

    public static QueryPartyroomSummaryResponse from(Partyroom partyroom, Crew crew, ProfileSettingDto profileSettingDto) {
        return new QueryPartyroomSummaryResponse(partyroom.getTitle(), partyroom.getIntroduction(),
                partyroom.getLinkDomain(), partyroom.getPlaybackTimeLimit(),
                new CurrentDjWithProfileDto(crew.getId(), profileSettingDto.getNickname(), profileSettingDto.getAvatarIconUri()));
    }
}
