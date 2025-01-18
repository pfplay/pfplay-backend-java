package com.pfplaybackend.api.party.application.dto.result;

import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomWithCrewDto;
import com.pfplaybackend.api.party.domain.entity.data.history.CrewBlockHistoryData;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class BlockedCrewResult {

    private Long blockId;
    private Long blockedCrewId;
    private String nickname;
    private String avatarIconUri;

    public static BlockedCrewResult from(Long blockId, CrewId crewId, ProfileSettingDto profileSettingDto) {
        return BlockedCrewResult.builder()
                .blockId(blockId)
                .blockedCrewId(crewId.getId())
                .nickname(profileSettingDto.getNickname())
                .avatarIconUri(profileSettingDto.getAvatarIconUri())
                .build();
    }
}
