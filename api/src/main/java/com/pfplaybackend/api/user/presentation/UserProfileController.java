package com.pfplaybackend.api.user.presentation;

import com.pfplaybackend.api.user.application.dto.command.UpdateBioCommand;
import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSummaryDto;
import com.pfplaybackend.api.user.application.service.UserProfileService;
import com.pfplaybackend.api.user.presentation.payload.request.UpdateMyBioRequest;
import com.pfplaybackend.api.user.presentation.payload.request.GetOtherProfileSummaryRequest;
import com.pfplaybackend.api.user.presentation.payload.response.MyProfileSummaryResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Profile API", description = "Operations related to user's profile management")
@RequestMapping("/api/v1/users")
@RestController
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    /**
     * 호출한(인증된) 사용자의 프로필 리소스를 조회한다.
     */
    @GetMapping("/me/profile/summary")
    @PreAuthorize("hasAnyRole('ROLE_GUEST', 'ROLE_MEMBER')")
    public ResponseEntity<?> getMyProfileSummary() {
        ProfileSummaryDto profileSummaryDto = userProfileService.getMyProfileSummary();
        return ResponseEntity.ok().body(ApiCommonResponse.success(MyProfileSummaryResponse.from(profileSummaryDto)));
    }

    /**
     * 호출한(인증된) 사용자의 프로필 리소스 내 Bio 리소스를 갱신한다.
     * @param request
     * @return
     */
    @PutMapping("/me/profile/bio")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity<?> updateMyBio(@RequestBody UpdateMyBioRequest request) {
        UpdateBioCommand updateBioCommand = new UpdateBioCommand(request.getNickname(), request.getIntroduction());
        userProfileService.updateMyBio(updateBioCommand);
        return ResponseEntity.ok().body(ApiCommonResponse.success("OK"));
    }

    /**
     * 타인의 프로필 리소스를 조회한다.
     * 자신의 프로필 리소스를 호출했던 것과 비교했을 때
     * 응답 객체의 필드 구성이 달라질 수 있음에 주의한다.
     * @param uid
     * @return
     */
    @GetMapping("/{uid}/profile/summary")
    @PreAuthorize("hasAnyRole('ROLE_GUEST', 'ROLE_MEMBER')")
    public ResponseEntity<?> getOtherProfileSummary(@PathVariable String uid,
                                                    @RequestBody GetOtherProfileSummaryRequest getOtherProfileSummaryRequest) {
        return ResponseEntity.ok().body(ApiCommonResponse.success("OK"));
    }
}
