package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.common.config.swagger.ApiErrorCodes;
import com.pfplaybackend.api.common.domain.value.PlaylistId;
import com.pfplaybackend.api.party.adapter.in.web.payload.request.dj.RegisterDjRequest;
import com.pfplaybackend.api.party.application.service.DjCommandService;
import com.pfplaybackend.api.party.domain.exception.DjException;
import com.pfplaybackend.api.party.domain.exception.GradeException;
import com.pfplaybackend.api.party.domain.value.DjId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 이 클래스는 특정 파티룸 내에서의 DJ 관리에 대한 표현 계층을 담당한다.
 */
@Tag(name = "DJ API")
@RequestMapping("/api/v1/partyrooms")
@RestController
@RequiredArgsConstructor
public class DjCommandController {

    private final DjCommandService djCommandService;

    @Operation(summary = "DJ 등록", description = "파티룸의 DJ 큐에 등록합니다. 플레이리스트를 선택하여 등록하며, MEMBER 권한이 필요합니다.")
    @SecurityRequirement(name = "cookieAuth")
    @ApiErrorCodes({DjException.class})
    @PostMapping("/{partyroomId}/djs")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER')")
    public ResponseEntity<ApiCommonResponse<Void>> enqueueDj(
            @Parameter(description = "파티룸 ID") @PathVariable Long partyroomId,
            @RequestBody RegisterDjRequest request) {
        djCommandService.enqueueDj(new PartyroomId(partyroomId), new PlaylistId(request.getPlaylistId()));
        return ResponseEntity.ok()
                .body(ApiCommonResponse.ok());
    }

    @Operation(summary = "본인 DJ 해제", description = "본인을 DJ 큐에서 해제합니다. 현재 재생 중인 DJ인 경우 재생이 중단됩니다.")
    @SecurityRequirement(name = "cookieAuth")
    @DeleteMapping("/{partyroomId}/djs/me")
    public ResponseEntity<ApiCommonResponse<Void>> dequeueDj(
            @Parameter(description = "파티룸 ID") @PathVariable Long partyroomId) {
        djCommandService.dequeueDj(new PartyroomId(partyroomId));
        return ResponseEntity.ok()
                .body(ApiCommonResponse.ok());
    }

    @Operation(summary = "타인 DJ 강제 해제", description = "관리자 권한으로 특정 DJ를 큐에서 강제 해제합니다. COMMUNITY_MANAGER 이상의 등급이 필요합니다.")
    @SecurityRequirement(name = "cookieAuth")
    @ApiErrorCodes({DjException.class, GradeException.class})
    @DeleteMapping("/{partyroomId}/djs/{djId}")
    public ResponseEntity<ApiCommonResponse<Void>> dequeueDj(
            @Parameter(description = "파티룸 ID") @PathVariable Long partyroomId,
            @Parameter(description = "해제할 DJ ID") @PathVariable Long djId) {
        djCommandService.dequeueDj(new PartyroomId(partyroomId), new DjId(djId));
        return ResponseEntity.ok()
                .body(ApiCommonResponse.ok());
    }
}
