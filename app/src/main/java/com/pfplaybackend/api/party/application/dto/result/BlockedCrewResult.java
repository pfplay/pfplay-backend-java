package com.pfplaybackend.api.party.application.dto.result;

import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;

public record BlockedCrewResult(
        Long blockId,
        Long blockedCrewId,
        String nickname,
        String avatarIconUri
) {
    public static BlockedCrewResult from(Long blockId, CrewId crewId, ProfileSettingDto profileSettingDto) {
        return new BlockedCrewResult(
                blockId,
                crewId.getId(),
                profileSettingDto.nickname(),
                profileSettingDto.avatarIconUri()
        );
    }
}
