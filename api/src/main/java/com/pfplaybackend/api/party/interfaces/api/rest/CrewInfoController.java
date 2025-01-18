package com.pfplaybackend.api.party.interfaces.api.rest;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.party.application.service.CrewInfoService;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSummaryDto;
import com.pfplaybackend.api.user.domain.value.UserId;
import com.pfplaybackend.api.user.presentation.payload.request.GetOtherProfileSummaryRequest;
import com.pfplaybackend.api.user.presentation.payload.response.OtherProfileSummaryResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Crew API")
@RequestMapping("/api/v1/crews")
@RestController
@RequiredArgsConstructor
public class CrewInfoController {

    private final CrewInfoService crewInfoService;

    @GetMapping("/{crewId}/profile/summary")
    @PreAuthorize("hasAnyRole('ROLE_GUEST', 'ROLE_MEMBER')")
    public ResponseEntity<?> getOtherProfileSummary(@PathVariable Long crewId) {
        return ResponseEntity.ok().body(ApiCommonResponse.success(crewInfoService.getProfileSummaryByCrewId(crewId)));
    }
}
