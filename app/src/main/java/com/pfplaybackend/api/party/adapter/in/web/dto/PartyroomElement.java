package com.pfplaybackend.api.party.adapter.in.web.dto;

import com.pfplaybackend.api.party.domain.enums.StageType;

import java.util.List;
import java.util.Map;

public record PartyroomElement(
        long partyroomId,
        StageType stageType,
        String title,
        String introduction,
        boolean isPlaybackActivated,
        long crewCount,
        Map<String, Object> playback,
        List<Map<String, Object>> primaryIcons
) {
}
