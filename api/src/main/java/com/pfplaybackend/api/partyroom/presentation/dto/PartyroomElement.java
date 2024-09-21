package com.pfplaybackend.api.partyroom.presentation.dto;

import com.pfplaybackend.api.partyroom.domain.enums.StageType;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Builder
@Data
public class PartyroomElement {
    private long partyroomId;
    private StageType stageType;
    private String title;
    private String introduction;
    boolean isPlaybackActivated;
    private long crewCount;
    private Map<String, Object> playback;
    private List<Map<String, Object>> primaryIcons;
}