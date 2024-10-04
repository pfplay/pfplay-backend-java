package com.pfplaybackend.api.partyroom.presentation.payload.response.info;

import com.pfplaybackend.api.partyroom.application.dto.partyroom.PartyroomWithCrewDto;
import com.pfplaybackend.api.partyroom.presentation.dto.PartyroomElement;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
@Getter
@Data
public class QueryPartyroomListResponse {
    public static List<PartyroomElement> from(List<PartyroomWithCrewDto> partyrooms, Map<UserId, ProfileSettingDto> profileSettings) {
        return partyrooms.stream().map(partyroomWithCrewDto -> {
            Map<String, Object> playback = null;
            if(partyroomWithCrewDto.isPlaybackActivated()) {
                playback = new HashMap<>();
                playback.put("name", partyroomWithCrewDto.getPlaybackDto().getName());
                playback.put("thumbnailImage", partyroomWithCrewDto.getPlaybackDto().getThumbnailImage());
            }
            List<Map<String, Object>> primaryIcons = partyroomWithCrewDto.getCrews().stream().map(crewDto -> profileSettings.get(crewDto.getUserId()))
                    .map(profileSettingDto -> {
                        Map<String, Object> primaryAvatar = new HashMap<>();
                        primaryAvatar.put("avatarIconUri", profileSettingDto.getAvatarIconUri());
                        return primaryAvatar;
                    }).toList();
            return PartyroomElement.builder()
                    .partyroomId(partyroomWithCrewDto.getPartyroomId())
                    .stageType(partyroomWithCrewDto.getStageType())
                    .title(partyroomWithCrewDto.getTitle())
                    .introduction(partyroomWithCrewDto.getIntroduction())
                    .crewCount(partyroomWithCrewDto.getCrewCount())
                    .isPlaybackActivated(partyroomWithCrewDto.isPlaybackActivated())
                    .playback(playback)
                    .primaryIcons(primaryIcons)
                    .build();
        }).toList();
    }
}