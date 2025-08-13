package com.pfplaybackend.api.user.presentation.payload.response;

import com.pfplaybackend.api.profile.domain.enums.AvatarCompositionType;
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
    private AvatarCompositionType avatarCompositionType;
    private String avatarBodyUri;
    private String avatarFaceUri;
    private String avatarIconUri;
    private int combinePositionX;
    private int combinePositionY;
    private double offsetX;
    private double offsetY;
    private double scale;
    private String walletAddress;
    private List<ActivitySummaryDto> activitySummaries;

    public static MyProfileSummaryResponse from(ProfileSummaryDto profileSummaryDto) {
        return MyProfileSummaryResponse.builder()
                .nickname(profileSummaryDto.getNickname())
                .introduction(profileSummaryDto.getIntroduction())
                .avatarBodyUri(profileSummaryDto.getAvatarBodyUri())
                .avatarCompositionType(profileSummaryDto.getAvatarCompositionType())
                .combinePositionX(profileSummaryDto.getCombinePositionX())
                .combinePositionY(profileSummaryDto.getCombinePositionY())
                .offsetX(profileSummaryDto.getOffsetX())
                .offsetY(profileSummaryDto.getOffsetY())
                .scale(profileSummaryDto.getScale())
                .avatarFaceUri(profileSummaryDto.getAvatarFaceUri())
                .avatarIconUri(profileSummaryDto.getAvatarIconUri())
                .walletAddress(profileSummaryDto.getWalletAddress())
                .activitySummaries(profileSummaryDto.getActivitySummaries())
                .build();
    }
}
