package com.pfplaybackend.api.partyroom.presentation;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.partyroom.application.service.DjManagementService;
import com.pfplaybackend.api.partyroom.domain.value.DjId;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.domain.value.PlaylistId;
import com.pfplaybackend.api.partyroom.presentation.payload.request.dj.RegisterDjRequest;
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
public class DjManagementController {

    private final DjManagementService djManagementService;

    /**
     *
     * @param partyroomId
     * @param request
     */
    @PostMapping("/{partyroomId}/djs")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER')")
    public ResponseEntity<?> enqueueDj(@PathVariable Long partyroomId,
                                       @RequestBody RegisterDjRequest request) {
        djManagementService.enqueueDj(new PartyroomId(partyroomId), new PlaylistId(request.getPlaylistId()));
        return ResponseEntity.ok()
                .body(ApiCommonResponse.success("OK"));
    }

    /**
     *
     * @param partyroomId
     */
    @DeleteMapping("/{partyroomId}/djs/me")
    public ResponseEntity<?> dequeueDj(@PathVariable Long partyroomId) {
        djManagementService.dequeueDj(new PartyroomId(partyroomId));
        return ResponseEntity.ok()
                .body(ApiCommonResponse.success("OK"));
    }

    /**
     *
     * @param partyroomId
     */
    @DeleteMapping("/{partyroomId}/djs/{djId}")
    public ResponseEntity<?> dequeueDj(@PathVariable Long partyroomId, @PathVariable Long djId) {
        djManagementService.dequeueDj(new PartyroomId(partyroomId), new DjId(djId));
        return ResponseEntity.ok()
                .body(ApiCommonResponse.success("OK"));
    }
}