package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.party.application.service.DjCommandService;
import com.pfplaybackend.api.party.domain.value.DjId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.common.domain.value.PlaylistId;
import com.pfplaybackend.api.party.adapter.in.web.payload.request.dj.RegisterDjRequest;
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

    /**
     *
     * @param partyroomId
     * @param request
     */
    @PostMapping("/{partyroomId}/djs")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER')")
    public ResponseEntity<ApiCommonResponse<Void>> enqueueDj(@PathVariable Long partyroomId,
                                                            @RequestBody RegisterDjRequest request) {
        djCommandService.enqueueDj(new PartyroomId(partyroomId), new PlaylistId(request.getPlaylistId()));
        return ResponseEntity.ok()
                .body(ApiCommonResponse.ok());
    }

    /**
     *
     * @param partyroomId
     */
    @DeleteMapping("/{partyroomId}/djs/me")
    public ResponseEntity<ApiCommonResponse<Void>> dequeueDj(@PathVariable Long partyroomId) {
        djCommandService.dequeueDj(new PartyroomId(partyroomId));
        return ResponseEntity.ok()
                .body(ApiCommonResponse.ok());
    }

    /**
     *
     * @param partyroomId
     */
    @DeleteMapping("/{partyroomId}/djs/{djId}")
    public ResponseEntity<ApiCommonResponse<Void>> dequeueDj(@PathVariable Long partyroomId, @PathVariable Long djId) {
        djCommandService.dequeueDj(new PartyroomId(partyroomId), new DjId(djId));
        return ResponseEntity.ok()
                .body(ApiCommonResponse.ok());
    }
}