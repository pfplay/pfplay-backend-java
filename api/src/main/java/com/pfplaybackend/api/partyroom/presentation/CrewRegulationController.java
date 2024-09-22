package com.pfplaybackend.api.partyroom.presentation;

import com.pfplaybackend.api.partyroom.application.service.CrewBlockService;
import com.pfplaybackend.api.partyroom.application.service.CrewGradeService;
import com.pfplaybackend.api.partyroom.application.service.CrewPenaltyService;
import com.pfplaybackend.api.partyroom.domain.value.CrewId;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.presentation.payload.request.UpdateCrewGradeRequest;
import com.pfplaybackend.api.partyroom.presentation.payload.request.UpdateCrewPenaltyRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
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
                            @RequestBody UpdateCrewGradeRequest request) {
        crewGradeService.updateGrade(new PartyroomId(partyroomId), new CrewId(crewId), request);
    }

    /**
     * 특정 크루에게 페널티를 부과하거나 해제한다.
     * @param partyroomId
     * @param crewId
     * @param request
     */
    @PutMapping("/{partyroomId}/crews/{crewId}/penalties")
    public void updateCrewPenalty(@PathVariable("partyroomId") long partyroomId, @PathVariable("crewId") long crewId,
                                    @RequestBody UpdateCrewPenaltyRequest request) {
        crewPenaltyService.updatePenalty(new PartyroomId(partyroomId), new CrewId(crewId), request);
    }
}