package com.pfplaybackend.api.partyroom.presentation.payload.response;

import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Builder
@Getter
@Data
public class QueryPartyroomListResponse {
    private String title;
    private String introduction;
    private int memberCount;
    private Map<String, String> music;
    private List<Object> primaryAvatars;

    public static QueryPartyroomListResponse from() {
        return QueryPartyroomListResponse.builder().build();
    }
}