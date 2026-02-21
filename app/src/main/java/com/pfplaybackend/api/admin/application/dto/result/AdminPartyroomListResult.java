package com.pfplaybackend.api.admin.application.dto.result;

import java.util.List;

public record AdminPartyroomListResult(List<PartyroomItem> partyrooms) {

    public record PartyroomItem(
            Long partyroomId,
            String stageType,
            String title,
            String linkDomain,
            Integer crewCount,
            Integer djCount,
            Boolean isPlaybackActivated
    ) {}
}
