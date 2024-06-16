package com.pfplaybackend.api.user.presentation.payload.response;

import com.pfplaybackend.api.user.application.dto.shared.ActivitySummaryDto;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSummaryDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MyProfileSummaryResponse {
    private String nickName;
    private String introduction;
    private String avatarBodyId;
    private String avatarFaceUri;
    private String walletAddress;
    private List<ActivitySummaryDto> activitySummaries;

    public static MyProfileSummaryResponse from(ProfileSummaryDto profileSummaryDto) {
        return MyProfileSummaryResponse.builder()
                .nickName(profileSummaryDto.getNickName())
                .introduction(profileSummaryDto.getIntroduction())
                .avatarBodyId(profileSummaryDto.getAvatarBodyId())
                .avatarFaceUri(profileSummaryDto.getAvatarFaceUri())
                .walletAddress(profileSummaryDto.getWalletAddress())
                .activitySummaries(profileSummaryDto.getActivitySummaries())
                .build();
    }
}
