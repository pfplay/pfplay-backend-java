package com.pfplaybackend.api.admin.application.dto.result;

import java.util.List;

public record DemoEnvironmentResult(
        Integer totalMembers,
        Integer specialMembers,
        Integer totalPartyrooms,
        Integer totalDjsRegistered,
        Long executionTimeMs,
        PartyroomDetail mainStage,
        List<PartyroomDetail> generalRooms
) {
    public record PartyroomDetail(
            Long partyroomId,
            String stageType,
            String title,
            String linkDomain,
            String hostUserId,
            Integer totalCrewCount,
            String djUserId,
            Long playlistId
    ) {}
}
