package com.pfplaybackend.api.user.application.dto.shared;

import com.pfplaybackend.api.common.domain.enums.AvatarCompositionType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ProfileSummaryDto {
    private String nickname;
    private String introduction;
    private String avatarBodyUri;
    private AvatarCompositionType avatarCompositionType;
    private int combinePositionX;
    private int combinePositionY;
    private double offsetX;
    private double offsetY;
    private double scale;
    private String avatarFaceUri;
    private String avatarIconUri;
    private String walletAddress;
    private List<ActivitySummaryDto> activitySummaries;
}
