package com.pfplaybackend.api.partyroom.application.peer;

import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import com.pfplaybackend.api.user.domain.value.UserId;

import java.util.List;

public interface UserProfilePeerService {
    List<ProfileSettingDto> getUsersProfileSetting(List<UserId> userIds);
}
