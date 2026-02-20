package com.pfplaybackend.api.admin.adapter.in.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Response DTO for demo environment initialization
 */
@Getter
@Builder
@AllArgsConstructor
public class DemoEnvironmentResponse {

    /**
     * Total number of virtual members created
     */
    private Integer totalMembers;

    /**
     * Number of special members (with playlists)
     */
    private Integer specialMembers;

    /**
     * Total number of partyrooms created
     */
    private Integer totalPartyrooms;

    /**
     * Number of DJs registered in queues
     */
    private Integer totalDjsRegistered;

    /**
     * Total execution time in milliseconds
     */
    private Long executionTimeMs;

    /**
     * Main stage information
     */
    private PartyroomDetail mainStage;

    /**
     * List of general partyrooms
     */
    private List<PartyroomDetail> generalRooms;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class PartyroomDetail {
        private Long partyroomId;
        private String stageType;
        private String title;
        private String linkDomain;
        private String hostUserId;
        private Integer totalCrewCount;
        private String djUserId;  // User ID of DJ in queue (null if no DJ)
        private Long playlistId;  // Playlist ID used by DJ (null if no DJ)
    }
}
