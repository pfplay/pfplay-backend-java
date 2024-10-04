package com.pfplaybackend.api.partyroom.presentation.payload.response.access;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
@Builder
public class PartyroomSharedLinkResponse {
    private String sharedLink;
}
