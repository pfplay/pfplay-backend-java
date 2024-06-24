package com.pfplaybackend.api.user.application.dto.shared;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ProfileSummaryDto {
    private String nickname;
    private String introduction;
    private String avatarBodyUri;
    private String avatarFaceUri;
    private String walletAddress;
    private List<ActivitySummaryDto> activitySummaries;
}
