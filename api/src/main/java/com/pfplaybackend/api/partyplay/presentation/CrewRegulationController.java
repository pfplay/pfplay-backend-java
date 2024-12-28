package com.pfplaybackend.api.partyplay.presentation;

import com.pfplaybackend.api.partyroom.application.service.CrewBlockService;
import com.pfplaybackend.api.partyroom.application.service.CrewGradeService;
import com.pfplaybackend.api.partyroom.application.service.CrewPenaltyService;
import com.pfplaybackend.api.partyroom.domain.value.CrewId;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.presentation.payload.request.regulation.AdjustGradeRequest;
import com.pfplaybackend.api.partyroom.presentation.payload.request.regulation.PunishPenaltyRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 이 클래스는 파티룸 내에서의 파티원 간의 규제 활동에 대한 표현 계층을 담당한다.
 * 여기서 말하는 규제 활동이란 사용자 간의 등급을 조정하거나 페널티를 부과하는 행위 등을 의미한다.
 */
@Tag(name = "Partyroom API")
@RequestMapping("/api/v1/partyrooms")
@RestController
@RequiredArgsConstructor
public class CrewRegulationController {

    final private CrewGradeService crewGradeService;
    final private CrewPenaltyService crewPenaltyService;
    final private CrewBlockService crewBlockService;

    /**
     * 특정 크루의 (해당 파티룸 내에서의) 등급을 조정한다.
     * @param partyroomId
     * @param crewId
     * @param request
     */
    @PutMapping("/{partyroomId}/crews/{crewId}/grade")
    public void updateCrewGrade(@PathVariable("partyroomId") long partyroomId,
                            @PathVariable("crewId") long crewId,
                            @RequestBody AdjustGradeRequest request) {
        crewGradeService.updateGrade(new PartyroomId(partyroomId), new CrewId(crewId), request);
    }

    /**
     * 특정 크루에게 페널티를 부과한다.
     * @param partyroomId
     * @param crewId
     * @param request
     */
    @PostMapping("/{partyroomId}/crews/{crewId}/penalties")
    public void imposeCrewPenalty(@PathVariable("partyroomId") Long partyroomId, @PathVariable("crewId") Long crewId,
                                  @Valid @RequestBody PunishPenaltyRequest request) {
        crewPenaltyService.addPenalty(new PartyroomId(partyroomId), new CrewId(crewId), request);
    }

    /**
     * 특정 크루의 기 부과된 페널티를 해제한다.
     * @param partyroomId
     * @param crewId
     * @param penaltyId
     */
    @DeleteMapping("/{partyroomId}/crews/{crewId}/penalties/{penaltyId}")
    public void releaseCrewPenalty(@PathVariable("partyroomId") long partyroomId,
                                  @PathVariable("crewId") long crewId,
                                  @PathVariable("penaltyId") long penaltyId) {
        //
    }


    /**
     * 특정 크루의 채팅을 차단한다.
     * @param partyroomId
     * @param blockedCrewId
     */
    @PostMapping("/{partyroomId}/crews/me/blocks/{blockedCrewId}")
    public void blockCrew(@PathVariable("partyroomId") long partyroomId,
                                @PathVariable("blockedCrewId") long blockedCrewId) {
    }

    /**
     * 특정 크루의 채팅 차단을 해제한다.
     * @param partyroomId
     * @param blockedCrewId
     */
    @DeleteMapping("/{partyroomId}/crews/me/blocks/{blockedCrewId}")
    public void unblockCrew(@PathVariable("partyroomId") long partyroomId,
                            @PathVariable("blockedCrewId") long blockedCrewId) {
    }
}