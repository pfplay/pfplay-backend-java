package com.pfplaybackend.api.user.adapter.in.web.payload.response;

import com.pfplaybackend.api.user.application.dto.shared.ActivitySummaryDto;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSummaryDto;
import com.pfplaybackend.api.user.domain.enums.ActivityType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Data
@Builder
public class OtherProfileSummaryResponse {
    private String nickname;
    private String introduction;
    private String avatarBodyUri;
    private String avatarFaceUri;
    private List<ActivitySummaryDto> activitySummaries;

    public static OtherProfileSummaryResponse from(ProfileSummaryDto profileSummaryDto) {
        List<ActivitySummaryDto> activitySummaries = Optional.ofNullable(profileSummaryDto.getActivitySummaries())
                .map(list -> list.stream()
                        .filter(a -> a.getActivityType().equals(ActivityType.DJ_PNT))
                        .toList())
                .orElse(List.of());
        return OtherProfileSummaryResponse.builder()
                .nickname(profileSummaryDto.getNickname())
                .introduction(profileSummaryDto.getIntroduction())
                .avatarBodyUri(profileSummaryDto.getAvatarBodyUri())
                .avatarFaceUri(profileSummaryDto.getAvatarFaceUri())
                .activitySummaries(activitySummaries)
                .build();
    }
}
