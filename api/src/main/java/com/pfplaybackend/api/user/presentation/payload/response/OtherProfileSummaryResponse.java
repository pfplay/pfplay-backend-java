package com.pfplaybackend.api.user.presentation.payload.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Builder
public class OtherProfileSummaryResponse {
    private String nickname;
    private String introduction;
    private String faceUrl;
    private Integer bodyId;
    private String bodyUrl;
    private String walletAddress;
}
