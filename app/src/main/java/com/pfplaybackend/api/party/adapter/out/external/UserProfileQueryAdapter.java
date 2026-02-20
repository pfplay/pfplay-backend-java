package com.pfplaybackend.api.party.adapter.out.external;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.party.application.port.out.UserProfileQueryPort;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSummaryDto;
import com.pfplaybackend.api.profile.application.service.UserProfileService;
import com.pfplaybackend.api.common.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserProfileQueryAdapter implements UserProfileQueryPort {

    private final UserProfileService userProfileService;

    @Override
    public Map<UserId, ProfileSettingDto> getUsersProfileSetting(List<UserId> userIds) {
        return userProfileService.getUsersProfileSetting(userIds);
    }

    @Override
    public ProfileSettingDto getUserProfileSetting(UserId userId) {
        return userProfileService.getUserProfileSetting(userId);
    }

    @Override
    public ProfileSummaryDto getOtherProfileSummary(UserId userId, AuthorityTier authorityTier) {
        return userProfileService.getOtherProfileSummary(userId, authorityTier);
    }
}
