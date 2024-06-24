package com.pfplaybackend.api.user.presentation.payload.response;

import com.pfplaybackend.api.user.application.dto.shared.ActivitySummaryDto;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSummaryDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MyProfileSummaryResponse {
    private String nickname;
    private String introduction;
    private String avatarBodyUri;
    private String avatarFaceUri;
    private String walletAddress;
    private List<ActivitySummaryDto> activitySummaries;

    public static MyProfileSummaryResponse from(ProfileSummaryDto profileSummaryDto) {
        return MyProfileSummaryResponse.builder()
                .nickname(profileSummaryDto.getNickname())
                .introduction(profileSummaryDto.getIntroduction())
                .avatarBodyUri(profileSummaryDto.getAvatarBodyUri())
                .avatarFaceUri(profileSummaryDto.getAvatarFaceUri())
                .walletAddress(profileSummaryDto.getWalletAddress())
                .activitySummaries(profileSummaryDto.getActivitySummaries())
                .build();
    }
}
