package com.pfplaybackend.api.partyroom.presentation.payload.response;

import com.pfplaybackend.api.partyroom.application.dto.PartyroomDto;
import com.pfplaybackend.api.partyroom.application.dto.PartyroomWithMemberDto;
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
    public static List<PartyroomElement> from(List<PartyroomWithMemberDto> partyrooms, Map<UserId, ProfileSettingDto> profileSettings) {
        return partyrooms.stream().map(partyroomWithMemberDto -> {
            Map<String, Object> playback = null;
            if(partyroomWithMemberDto.isPlaybackActivated()) {
                playback = new HashMap<>();
                playback.put("name", partyroomWithMemberDto.getPlaybackDto().getName());
                playback.put("thumbnailImage", partyroomWithMemberDto.getPlaybackDto().getThumbnailImage());
            }
            List<Map<String, Object>> primaryIcons = partyroomWithMemberDto.getMembers().stream().map(partymemberDto -> profileSettings.get(partymemberDto.getUserId()))
                    .map(profileSettingDto -> {
                        Map<String, Object> primaryAvatar = new HashMap<>();
                        primaryAvatar.put("avatarIconUri", profileSettingDto.getAvatarIconUri());
                        return primaryAvatar;
                    }).toList();
            return PartyroomElement.builder()
                    .partyroomId(partyroomWithMemberDto.getPartyroomId())
                    .stageType(partyroomWithMemberDto.getStageType())
                    .title(partyroomWithMemberDto.getTitle())
                    .introduction(partyroomWithMemberDto.getIntroduction())
                    .memberCount(partyroomWithMemberDto.getMemberCount())
                    .isPlaybackActivated(partyroomWithMemberDto.isPlaybackActivated())
                    .playback(playback)
                    .primaryIcons(primaryIcons)
                    .build();
        }).toList();
    }
}