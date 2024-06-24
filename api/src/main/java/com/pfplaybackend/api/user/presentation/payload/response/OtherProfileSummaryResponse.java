package com.pfplaybackend.api.user.presentation.payload.response;

import com.pfplaybackend.api.user.application.dto.shared.ActivitySummaryDto;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSummaryDto;
import com.pfplaybackend.api.user.domain.enums.ActivityType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class OtherProfileSummaryResponse {
    private String nickname;
    private String introduction;
    private String avatarBodyUri;
    private String avatarFaceUri;
    private List<ActivitySummaryDto> activitySummaries;

    public static OtherProfileSummaryResponse from(ProfileSummaryDto profileSummaryDto) {
        // TODO Code Refactoring
        List<ActivitySummaryDto> activitySummaries = null;
        if(profileSummaryDto.getActivitySummaries() != null) {
            activitySummaries = profileSummaryDto.getActivitySummaries().stream()
                    .filter(activitySummaryDto -> activitySummaryDto.getActivityType().equals(ActivityType.DJ_PNT))
                    .toList();
        }
        return OtherProfileSummaryResponse.builder()
                .nickname(profileSummaryDto.getNickname())
                .introduction(profileSummaryDto.getIntroduction())
                .avatarBodyUri(profileSummaryDto.getAvatarBodyUri())
                .avatarFaceUri(profileSummaryDto.getAvatarFaceUri())
                .activitySummaries(activitySummaries)
                .build();
    }
}
