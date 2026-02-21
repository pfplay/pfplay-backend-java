package com.pfplaybackend.api.admin.adapter.in.web.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Response DTO for bulk preview environment creation
 */
@Getter
@Builder
@AllArgsConstructor
public class CreateBulkPreviewEnvironmentResponse {

    /**
     * Total number of partyrooms created
     */
    private Integer totalPartyrooms;

    /**
     * Total number of virtual members created
     */
    private Integer totalVirtualMembers;

    /**
     * Total execution time in milliseconds
     */
    private Long executionTimeMs;

    /**
     * List of created partyrooms with their details
     */
    private List<PartyroomSummary> partyrooms;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class PartyroomSummary {
        private Long partyroomId;
        private String title;
        private String linkDomain;
        private String hostUserId;
        private Integer crewCount;
        private List<String> crewUserIds;
    }
}
