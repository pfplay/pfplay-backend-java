package com.pfplaybackend.api.user.adapter.in.web;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSummaryDto;
import com.pfplaybackend.api.user.application.service.UserProfileQueryService;
import com.pfplaybackend.api.user.adapter.in.web.payload.response.QueryMyProfileSummaryResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Profile API")
@RequestMapping("/api/v1/users")
@RestController
@RequiredArgsConstructor
public class UserProfileQueryController {

    private final UserProfileQueryService userProfileQueryService;

    /**
     * 호출한(인증된) 사용자의 프로필 리소스를 조회한다.
     */
    @GetMapping("/me/profile/summary")
    @PreAuthorize("hasAnyRole('ROLE_GUEST', 'ROLE_MEMBER')")
    public ResponseEntity<ApiCommonResponse<QueryMyProfileSummaryResponse>> getMyProfileSummary() {
        ProfileSummaryDto profileSummaryDto = userProfileQueryService.getMyProfileSummary();
        return ResponseEntity.ok().body(ApiCommonResponse.success(QueryMyProfileSummaryResponse.from(profileSummaryDto)));
    }
}