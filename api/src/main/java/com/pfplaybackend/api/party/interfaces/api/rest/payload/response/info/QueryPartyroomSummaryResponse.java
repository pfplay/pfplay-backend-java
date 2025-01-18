package com.pfplaybackend.api.party.interfaces.api.rest.payload.response.info;

import com.pfplaybackend.api.party.application.dto.dj.CurrentDjWithProfileDto;
import com.pfplaybackend.api.party.domain.entity.domainmodel.Crew;
import com.pfplaybackend.api.party.domain.entity.domainmodel.Partyroom;
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
        if(crew != null) {
            return new QueryPartyroomSummaryResponse(partyroom.getTitle(), partyroom.getIntroduction(),
                    partyroom.getLinkDomain(), partyroom.getPlaybackTimeLimit(),
                    new CurrentDjWithProfileDto(crew.getId(), profileSettingDto.getNickname(), profileSettingDto.getAvatarIconUri()));
        } else {
            return new QueryPartyroomSummaryResponse(partyroom.getTitle(), partyroom.getIntroduction(),
                    partyroom.getLinkDomain(), partyroom.getPlaybackTimeLimit(), null);
        }
    }
}
