package com.pfplaybackend.api.partyroom.application.proxy;

import com.pfplaybackend.api.partyroom.application.peer.UserProfilePeerService;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import com.pfplaybackend.api.user.application.service.UserProfileService;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserProfileProxyService implements UserProfilePeerService {

    private final UserProfileService userProfileService;

    @Override
    public Map<UserId, ProfileSettingDto> getUsersProfileSetting(List<UserId> userIds) {
        return userProfileService.getUsersProfileSetting(userIds);
    }
}
