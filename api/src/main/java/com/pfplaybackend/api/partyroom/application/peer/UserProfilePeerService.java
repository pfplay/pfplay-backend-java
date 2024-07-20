package com.pfplaybackend.api.partyroom.application.peer;

import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import com.pfplaybackend.api.user.domain.value.UserId;

import java.util.List;
import java.util.Map;

public interface UserProfilePeerService {
    Map<UserId, ProfileSettingDto> getUsersProfileSetting(List<UserId> userIds);
}
