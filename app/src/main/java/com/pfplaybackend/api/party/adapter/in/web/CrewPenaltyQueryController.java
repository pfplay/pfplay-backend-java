package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.common.config.swagger.ApiErrorCodes;
import com.pfplaybackend.api.party.application.dto.result.PenaltyResult;
import com.pfplaybackend.api.party.application.service.CrewPenaltyQueryService;
import com.pfplaybackend.api.party.domain.exception.PartyroomException;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Partyroom API")
@RequestMapping("/api/v1/partyrooms")
@RestController
@RequiredArgsConstructor
public class CrewPenaltyQueryController {

    final private CrewPenaltyQueryService crewPenaltyQueryService;

    @Operation(summary = "패널티 목록 조회", description = "파티룸에서 부과된 패널티 목록을 조회합니다.")
    @SecurityRequirement(name = "cookieAuth")
    @ApiErrorCodes({PartyroomException.class})
    @GetMapping("/{partyroomId}/penalties")
    public ResponseEntity<ApiCommonResponse<List<PenaltyResult>>> getAllPenalties(
            @Parameter(description = "파티룸 ID") @PathVariable("partyroomId") Long id) {
        return ResponseEntity.ok(
                ApiCommonResponse.success(crewPenaltyQueryService.getPenalties(PartyroomId.of(id))));
    }
}
