package com.pfplaybackend.api.partyroom.presentation;

import com.pfplaybackend.api.partyroom.application.service.PartymemberRegulationService;
import com.pfplaybackend.api.partyroom.presentation.payload.request.ModifyLevelRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 이 클래스는 파티룸 내에서의 파티원 간의 규제 활동에 대한 표현 계층을 담당한다.
 * 여기서 말하는 규제 활동이란 사용자 간의 등급을 조정하거나 페널티를 부과하는 행위 등을 의미한다.
 */
@Tag(name = "Partyroom Regulation API", description = "Operations related to partyroom management")
@RequestMapping("/api/v1/partyrooms")
@RestController
@RequiredArgsConstructor
public class PartymemberRegulationController {

    final private PartymemberRegulationService partymemberRegulationService;

    @PutMapping("/{partyroomId}/partymembers/{partymemberId}/level")
    public void modifyLevel(@PathVariable("partyroomId") Long partyroomId,
                            @PathVariable("partymemberId") UUID partymemberId,
                            ModifyLevelRequest modifyLevelRequest) {
        // TODO Q. 같은 레벨 사람에게도 강등시킬 수 있나?
    }

    @PostMapping("/{partyroomId}/partymembers/{partymemberId}/penalties")
    public void issuePenalty(@PathVariable("partyroomId") Long partyroomId, @PathVariable("partymemberId") UUID partymemberId) {
        // TODO Q. 같은 레벨 사람에게도 페널티를 부과할 수 있나?
    }
}
