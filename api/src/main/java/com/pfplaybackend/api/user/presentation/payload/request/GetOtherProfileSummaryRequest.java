package com.pfplaybackend.api.user.presentation.payload.request;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
public class GetOtherProfileSummaryRequest {
    private AuthorityTier authorityTier;
}
