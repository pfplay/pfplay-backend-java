package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.common.config.swagger.ApiErrorCodes;
import com.pfplaybackend.api.party.adapter.in.web.payload.request.regulation.ApplyPenaltyRequest;
import com.pfplaybackend.api.party.application.dto.command.PunishPenaltyCommand;
import com.pfplaybackend.api.party.application.service.CrewPenaltyCommandService;
import com.pfplaybackend.api.party.domain.exception.GradeException;
import com.pfplaybackend.api.party.domain.exception.PenaltyException;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 이 클래스는 파티룸 내에서의 파티원 간의 규제 활동에 대한 표현 계층을 담당한다.
 * 여기서 말하는 규제 활동이란 사용자 간의 등급을 조정하거나 페널티를 부과하는 행위 등을 의미한다.
 */
@Tag(name = "Partyroom API")
@RequestMapping("/api/v1/partyrooms")
@RestController
@RequiredArgsConstructor
public class CrewPenaltyCommandController {

    final private CrewPenaltyCommandService crewPenaltyCommandService;

    /**
     * 특정 크루에게 페널티를 부과한다.
     * @param partyroomId
     * @param request
     */
    @Operation(summary = "패널티 부과", description = "특정 크루에게 패널티를 부과합니다. COMMUNITY_MANAGER 이상의 등급이 필요합니다.")
    @SecurityRequirement(name = "cookieAuth")
    @ApiErrorCodes({GradeException.class})
    @PostMapping("/{partyroomId}/penalties")
    public ResponseEntity<Void> imposeCrewPenalty(
            @Parameter(description = "파티룸 ID") @PathVariable("partyroomId") Long partyroomId,
            @Valid @RequestBody ApplyPenaltyRequest request) {
        // TODO targetCrewId 정보를 요청 본문으로 이동
        crewPenaltyCommandService.addPenalty(new PartyroomId(partyroomId),
                new PunishPenaltyCommand(request.getCrewId(), request.getPenaltyType(), request.getDetail()));
        return ResponseEntity.accepted().build();
    }

    /**
     * 특정 크루의 기 부과된 페널티를 해제한다.
     * @param partyroomId
     * @param penaltyId
     */
    @Operation(summary = "패널티 해제", description = "기존에 부과된 패널티를 해제합니다. COMMUNITY_MANAGER 이상의 등급이 필요합니다.")
    @SecurityRequirement(name = "cookieAuth")
    @ApiErrorCodes({GradeException.class, PenaltyException.class})
    @DeleteMapping("/{partyroomId}/penalties/{penaltyId}")
    public ResponseEntity<Void> releaseCrewPenalty(
            @Parameter(description = "파티룸 ID") @PathVariable("partyroomId") Long partyroomId,
            @Parameter(description = "패널티 ID") @PathVariable("penaltyId") Long penaltyId) {
        // TODO targetCrewId, penaltyId 정보를 요청 본문으로 이동
        crewPenaltyCommandService.releaseCrewPenalty(new PartyroomId(partyroomId), penaltyId);
        return ResponseEntity.accepted().build();
    }
}
