package com.pfplaybackend.api.party.adapter.in.web.payload.response.info;

import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.party.application.dto.partyroom.PartyroomWithCrewDto;
import com.pfplaybackend.api.party.domain.enums.StageType;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Builder
@Getter
@Data
public class QueryPartyroomListResponse {

    public record PartyroomElement(
            long partyroomId,
            StageType stageType,
            String title,
            String introduction,
            boolean playbackActivated,
            long crewCount,
            PlaybackSummary playback,
            List<CrewIcon> primaryIcons
    ) {}

    public static List<PartyroomElement> from(List<PartyroomWithCrewDto> partyrooms, Map<UserId, ProfileSettingDto> profileSettings) {
        return partyrooms.stream().map(partyroomWithCrewDto -> {
            PlaybackSummary playback = null;
            if(partyroomWithCrewDto.playbackActivated()) {
                playback = PlaybackSummary.withoutDuration(partyroomWithCrewDto.playbackDto());
            }
            List<CrewIcon> primaryIcons = partyroomWithCrewDto.crews().stream()
                    .map(crewDto -> profileSettings.get(crewDto.userId()))
                    .map(profileSettingDto -> new CrewIcon(profileSettingDto.avatarIconUri()))
                    .toList();
            return new PartyroomElement(
                    partyroomWithCrewDto.partyroomId(),
                    partyroomWithCrewDto.stageType(),
                    partyroomWithCrewDto.title(),
                    partyroomWithCrewDto.introduction(),
                    partyroomWithCrewDto.playbackActivated(),
                    partyroomWithCrewDto.crewCount(),
                    playback,
                    primaryIcons
            );
        }).toList();
    }
}
