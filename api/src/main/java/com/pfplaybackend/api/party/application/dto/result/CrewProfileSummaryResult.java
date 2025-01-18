package com.pfplaybackend.api.party.application.dto.result;

import com.pfplaybackend.api.user.application.dto.shared.ActivitySummaryDto;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSummaryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class CrewProfileSummaryResult {
    private Long crewId;
    private String nickname;
    private String introduction;
    private String avatarBodyUri;
    private int combinePositionX;
    private int combinePositionY;
    private String avatarFaceUri;
    private List<ActivitySummaryDto> activitySummaries;

    public static CrewProfileSummaryResult from(Long crewId, ProfileSummaryDto dto) {
        return CrewProfileSummaryResult.builder()
                .crewId(crewId)
                .nickname(dto.getNickname())
                .introduction(dto.getIntroduction())
                .avatarBodyUri(dto.getAvatarBodyUri())
                .avatarFaceUri(dto.getAvatarFaceUri())
                .activitySummaries(dto.getActivitySummaries())
                .combinePositionX(dto.getCombinePositionX())
                .combinePositionY(dto.getCombinePositionY())
                .build();
    }
}
