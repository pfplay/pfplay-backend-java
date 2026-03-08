package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.common.config.swagger.ApiErrorCodes;
import com.pfplaybackend.api.party.adapter.in.web.payload.request.AddBlockRequest;
import com.pfplaybackend.api.party.application.dto.command.AddBlockCommand;
import com.pfplaybackend.api.party.application.service.CrewBlockCommandService;
import com.pfplaybackend.api.party.domain.exception.BlockException;
import com.pfplaybackend.api.party.domain.exception.CrewException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Crew API")
@RequestMapping("/api/v1/crews")
@RestController
@RequiredArgsConstructor
public class CrewBlockCommandController {

    private final CrewBlockCommandService crewBlockCommandService;

    @Operation(summary = "크루 차단", description = "특정 크루를 차단합니다. 차단된 크루의 채팅 메시지가 숨겨집니다.")
    @SecurityRequirement(name = "cookieAuth")
    @ApiErrorCodes({BlockException.class, CrewException.class})
    @PostMapping("/me/blocks")
    public ResponseEntity<ApiCommonResponse<Void>> blockOtherCrew(@RequestBody AddBlockRequest request)  {
        crewBlockCommandService.addBlock(new AddBlockCommand(request.getCrewId()));
        return ResponseEntity.ok()
                .body(ApiCommonResponse.ok());
    }

    @Operation(summary = "크루 차단 해제", description = "기존에 차단한 크루의 차단을 해제합니다.")
    @SecurityRequirement(name = "cookieAuth")
    @ApiErrorCodes({BlockException.class})
    @DeleteMapping("/me/blocks/{blockId}")
    public ResponseEntity<ApiCommonResponse<Void>> unblockOther(
            @Parameter(description = "차단 ID") @PathVariable Long blockId)  {
        crewBlockCommandService.removeBlock(blockId);
        return ResponseEntity.ok()
                .body(ApiCommonResponse.ok());
    }
}
