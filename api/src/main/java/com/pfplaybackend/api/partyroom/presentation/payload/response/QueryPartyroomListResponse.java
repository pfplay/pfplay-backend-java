package com.pfplaybackend.api.partyroom.presentation.payload.response;

import com.pfplaybackend.api.partyroom.application.dto.PartyroomDto;
import com.pfplaybackend.api.partyroom.presentation.dto.PartyroomElement;
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
    public static List<PartyroomElement> from(List<PartyroomDto> partyrooms) {
        return partyrooms.stream()
                .map(partyroomDto -> {
                    Map<String, Object> playback = null;
                    if(partyroomDto.isPlaybackActivated()) {
                        playback = new HashMap<>();
                        playback.put("name", partyroomDto.getPlaybackDto().getName());
                        playback.put("thumbnailImage", partyroomDto.getPlaybackDto().getThumbnailImage());
                    }
                    return PartyroomElement.builder()
                            .partyroomId(partyroomDto.getPartyroomId())
                            .stageType(partyroomDto.getStageType())
                            .title(partyroomDto.getTitle())
                            .introduction(partyroomDto.getIntroduction())
                            .memberCount(partyroomDto.getMemberCount())
                            .isPlaybackActivated(partyroomDto.isPlaybackActivated())
                            .playback(playback)
                            .build();
                })
                .toList();
    }
}