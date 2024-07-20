package com.pfplaybackend.api.partyroom.presentation.dto;

import com.pfplaybackend.api.partyroom.application.dto.PartyroomWithMemberDto;
import com.pfplaybackend.api.partyroom.domain.enums.StageType;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
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
    private long memberCount;
    private Map<String, Object> playback;
    private List<Map<String, Object>> primaryIcons;
}