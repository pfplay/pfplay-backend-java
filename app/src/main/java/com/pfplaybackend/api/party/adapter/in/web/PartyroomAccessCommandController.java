package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.party.application.service.PartyroomAccessCommandService;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.adapter.in.web.payload.response.access.EnterPartyroomResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Partyroom API")
@RequestMapping("/api/v1/partyrooms")
@RestController
@RequiredArgsConstructor
public class PartyroomAccessCommandController {

    private final PartyroomAccessCommandService partyroomAccessCommandService;

    @PostMapping("/{partyroomId}/enter")
    public ResponseEntity<ApiCommonResponse<EnterPartyroomResponse>> enterPartyroom(
            @PathVariable Long partyroomId) {
        CrewData crew = partyroomAccessCommandService.tryEnter(new PartyroomId(partyroomId));
        return ResponseEntity.ok().body(ApiCommonResponse.success(EnterPartyroomResponse.from(crew)));
    }

    @PostMapping("/{partyroomId}/exit")
    public ResponseEntity<Void> exitPartyroom(
            @PathVariable Long partyroomId) {
        partyroomAccessCommandService.exit(new PartyroomId(partyroomId));
        return ResponseEntity.ok().build();
    }
}
