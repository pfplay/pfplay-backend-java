package com.pfplaybackend.api.admin.application.dto.result;

import java.util.List;

public record BulkPreviewResult(
        Integer totalPartyrooms,
        Integer totalVirtualMembers,
        Long executionTimeMs,
        List<PartyroomSummary> partyrooms
) {
    public record PartyroomSummary(
            Long partyroomId,
            String title,
            String linkDomain,
            String hostUserId,
            Integer crewCount,
            List<String> crewUserIds
    ) {}
}
