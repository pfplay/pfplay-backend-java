package com.pfplaybackend.api.party.adapter.in.web.payload.response.access;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
@Builder
public class PartyroomSharedLinkResponse {
    private String sharedLink;
}
