package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.party.application.dto.command.PunishPenaltyCommand;
import com.pfplaybackend.api.party.application.dto.result.PenaltyResult;
import com.pfplaybackend.api.party.application.service.CrewPenaltyService;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.adapter.in.web.payload.request.regulation.PunishPenaltyRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 이 클래스는 파티룸 내에서의 파티원 간의 규제 활동에 대한 표현 계층을 담당한다.
 * 여기서 말하는 규제 활동이란 사용자 간의 등급을 조정하거나 페널티를 부과하는 행위 등을 의미한다.
 */
@Tag(name = "Partyroom API")
@RequestMapping("/api/v1/partyrooms")
@RestController
@RequiredArgsConstructor
public class CrewPenaltyController {

    final private CrewPenaltyService crewPenaltyService;

    @GetMapping("/{partyroomId}/penalties")
    public ResponseEntity<ApiCommonResponse<List<PenaltyResult>>> getAllPenalties(@PathVariable("partyroomId") Long id) {
        return ResponseEntity.ok(
                ApiCommonResponse.success(crewPenaltyService.getPenalties(PartyroomId.of(id))));
    }

    /**
     * 특정 크루에게 페널티를 부과한다.
     * @param partyroomId
     * @param request
     */
    @PostMapping("/{partyroomId}/penalties")
    public ResponseEntity<Void> imposeCrewPenalty(@PathVariable("partyroomId") Long partyroomId,
                                  @Valid @RequestBody PunishPenaltyRequest request) {
        // TODO targetCrewId 정보를 요청 본문으로 이동
        crewPenaltyService.addPenalty(new PartyroomId(partyroomId),
                new PunishPenaltyCommand(request.getCrewId(), request.getPenaltyType(), request.getDetail()));
        return ResponseEntity.accepted().build();
    }

    /**
     * 특정 크루의 기 부과된 페널티를 해제한다.
     * @param partyroomId
     * @param penaltyId
     */
    @DeleteMapping("/{partyroomId}/penalties/{penaltyId}")
    public ResponseEntity<Void> releaseCrewPenalty(@PathVariable("partyroomId") Long partyroomId,
                                   @PathVariable("penaltyId") Long penaltyId) {
        // TODO targetCrewId, penaltyId 정보를 요청 본문으로 이동
        crewPenaltyService.releaseCrewPenalty(new PartyroomId(partyroomId), penaltyId);
        return ResponseEntity.accepted().build();
    }
}
