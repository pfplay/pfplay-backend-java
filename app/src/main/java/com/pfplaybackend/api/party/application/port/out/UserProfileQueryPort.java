package com.pfplaybackend.api.party.application.port.out;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSummaryDto;
import com.pfplaybackend.api.common.domain.value.UserId;

import java.util.List;
import java.util.Map;

public interface UserProfileQueryPort {
    Map<UserId, ProfileSettingDto> getUsersProfileSetting(List<UserId> userIds);
    ProfileSettingDto getUserProfileSetting(UserId userId);
    ProfileSummaryDto getOtherProfileSummary(UserId userId, AuthorityTier authorityTier);
    AuthorityTier getAuthorityTier(UserId userId);
}
