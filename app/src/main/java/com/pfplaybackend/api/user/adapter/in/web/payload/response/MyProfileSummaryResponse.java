package com.pfplaybackend.api.user.adapter.in.web.payload.response;

import com.pfplaybackend.api.common.domain.enums.AvatarCompositionType;
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
                .nickname(profileSummaryDto.nickname())
                .introduction(profileSummaryDto.introduction())
                .avatarBodyUri(profileSummaryDto.avatarBodyUri())
                .avatarCompositionType(profileSummaryDto.avatarCompositionType())
                .combinePositionX(profileSummaryDto.combinePositionX())
                .combinePositionY(profileSummaryDto.combinePositionY())
                .offsetX(profileSummaryDto.offsetX())
                .offsetY(profileSummaryDto.offsetY())
                .scale(profileSummaryDto.scale())
                .avatarFaceUri(profileSummaryDto.avatarFaceUri())
                .avatarIconUri(profileSummaryDto.avatarIconUri())
                .walletAddress(profileSummaryDto.walletAddress())
                .activitySummaries(profileSummaryDto.activitySummaries())
                .build();
    }
}
