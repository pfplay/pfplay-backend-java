package com.pfplaybackend.api.party.adapter.in.web.payload.response.info;

import com.pfplaybackend.api.party.application.dto.partyroom.PartyroomWithCrewDto;
import com.pfplaybackend.api.party.adapter.in.web.dto.PartyroomElement;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import com.pfplaybackend.api.common.domain.value.UserId;
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
                playback.put("name", partyroomWithCrewDto.playbackDto().getName());
                playback.put("thumbnailImage", partyroomWithCrewDto.playbackDto().getThumbnailImage());
            }
            List<Map<String, Object>> primaryIcons = partyroomWithCrewDto.crews().stream().map(crewDto -> profileSettings.get(crewDto.userId()))
                    .map(profileSettingDto -> {
                        Map<String, Object> primaryAvatar = new HashMap<>();
                        primaryAvatar.put("avatarIconUri", profileSettingDto.avatarIconUri());
                        return primaryAvatar;
                    }).toList();
            return new PartyroomElement(
                    partyroomWithCrewDto.partyroomId(),
                    partyroomWithCrewDto.stageType(),
                    partyroomWithCrewDto.title(),
                    partyroomWithCrewDto.introduction(),
                    partyroomWithCrewDto.isPlaybackActivated(),
                    partyroomWithCrewDto.crewCount(),
                    playback,
                    primaryIcons
            );
        }).toList();
    }
}