package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.common.config.swagger.ApiErrorCodes;
import com.pfplaybackend.api.party.adapter.in.web.payload.response.access.EnterPartyroomResponse;
import com.pfplaybackend.api.party.application.service.PartyroomAccessCommandService;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.exception.CrewException;
import com.pfplaybackend.api.party.domain.exception.PartyroomException;
import com.pfplaybackend.api.party.domain.exception.PenaltyException;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Partyroom API")
@RequestMapping("/api/v1/partyrooms")
@RestController
@RequiredArgsConstructor
public class PartyroomAccessCommandController {

    private final PartyroomAccessCommandService partyroomAccessCommandService;

    @Operation(summary = "파티룸 입장", description = "파티룸에 입장합니다. 입장 시 크루(Crew)로 등록되며, 크루 정보가 반환됩니다.")
    @SecurityRequirement(name = "cookieAuth")
    @ApiErrorCodes({PartyroomException.class, PenaltyException.class})
    @PostMapping("/{partyroomId}/enter")
    public ResponseEntity<ApiCommonResponse<EnterPartyroomResponse>> enterPartyroom(
            @Parameter(description = "파티룸 ID") @PathVariable Long partyroomId) {
        CrewData crew = partyroomAccessCommandService.tryEnter(new PartyroomId(partyroomId));
        return ResponseEntity.ok().body(ApiCommonResponse.success(EnterPartyroomResponse.from(crew)));
    }

    @Operation(summary = "파티룸 퇴장", description = "현재 입장한 파티룸에서 퇴장합니다. DJ 큐에 등록된 경우 자동으로 해제됩니다.")
    @SecurityRequirement(name = "cookieAuth")
    @ApiErrorCodes({CrewException.class})
    @PostMapping("/{partyroomId}/exit")
    public ResponseEntity<Void> exitPartyroom(
            @Parameter(description = "파티룸 ID") @PathVariable Long partyroomId) {
        partyroomAccessCommandService.exit(new PartyroomId(partyroomId));
        return ResponseEntity.ok().build();
    }
}
