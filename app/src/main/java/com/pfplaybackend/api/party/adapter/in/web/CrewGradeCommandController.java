package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.common.config.swagger.ApiErrorCodes;
import com.pfplaybackend.api.party.adapter.in.web.payload.request.regulation.AdjustGradeRequest;
import com.pfplaybackend.api.party.application.dto.command.AdjustGradeCommand;
import com.pfplaybackend.api.party.application.service.CrewGradeCommandService;
import com.pfplaybackend.api.party.domain.exception.CrewException;
import com.pfplaybackend.api.party.domain.exception.GradeException;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
public class CrewGradeCommandController {

    private final CrewGradeCommandService crewGradeCommandService;

    /**
     * 특정 크루의 (해당 파티룸 내에서의) 등급을 조정한다.
     * @param partyroomId
     * @param crewId
     * @param request
     */
    @Operation(summary = "크루 등급 변경", description = "특정 크루의 파티룸 내 등급을 변경합니다. HOST 또는 COMMUNITY_MANAGER 등급이 필요합니다.")
    @ApiResponse(responseCode = "204", description = "크루 등급 변경 성공")
    @SecurityRequirement(name = "cookieAuth")
    @ApiErrorCodes({GradeException.class, CrewException.class})
    @PatchMapping("/{partyroomId}/crews/{crewId}/grade")
    public ResponseEntity<Void> updateCrewGrade(
            @Parameter(description = "파티룸 ID") @PathVariable("partyroomId") long partyroomId,
            @Parameter(description = "크루 ID") @PathVariable("crewId") long crewId,
            @Valid @RequestBody AdjustGradeRequest request) {
        crewGradeCommandService.updateGrade(new PartyroomId(partyroomId), new CrewId(crewId),
                new AdjustGradeCommand(request.getGradeType()));
        return ResponseEntity.noContent().build();
    }
}
