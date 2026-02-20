package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.party.application.dto.result.CrewProfileSummaryResult;
import com.pfplaybackend.api.party.application.service.PartyroomInfoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Crew API")
@RequestMapping("/api/v1/crews")
@RestController
@RequiredArgsConstructor
public class CrewInfoController {

    private final PartyroomInfoService partyroomInfoService;

    @GetMapping("/{crewId}/profile/summary")
    @PreAuthorize("hasAnyRole('ROLE_GUEST', 'ROLE_MEMBER')")
    public ResponseEntity<ApiCommonResponse<CrewProfileSummaryResult>> getOtherProfileSummary(@PathVariable Long crewId) {
        return ResponseEntity.ok().body(ApiCommonResponse.success(partyroomInfoService.getProfileSummaryByCrewId(crewId)));
    }
}
