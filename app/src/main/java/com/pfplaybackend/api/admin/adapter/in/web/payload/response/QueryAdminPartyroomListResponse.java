package com.pfplaybackend.api.admin.adapter.in.web.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class QueryAdminPartyroomListResponse {

    private List<PartyroomItem> partyrooms;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class PartyroomItem {
        private Long partyroomId;
        private String stageType;
        private String title;
        private String linkDomain;
        private Integer crewCount;
        private Integer djCount;
        private Boolean playbackActivated;
    }
}
