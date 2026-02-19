package com.pfplaybackend.api.party.interfaces.api.rest.payload.response.info;

import com.pfplaybackend.api.party.application.dto.dj.CurrentDjWithProfileDto;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
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

    public static QueryPartyroomSummaryResponse from(PartyroomData partyroom, CrewData crew, ProfileSettingDto profileSettingDto) {
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
