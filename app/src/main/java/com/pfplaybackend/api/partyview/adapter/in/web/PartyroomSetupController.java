package com.pfplaybackend.api.partyview.adapter.in.web;

import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.partyview.application.service.PartyroomSetupQueryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Partyroom API")
@RequestMapping("/api/v1/partyrooms")
@RestController
@RequiredArgsConstructor
public class PartyroomSetupController {

    private final PartyroomSetupQueryService partyroomSetupQueryService;

    @GetMapping("/{partyroomId}/setup")
    public ResponseEntity<?> getSetupInfo(@PathVariable Long partyroomId) {
        return ResponseEntity.ok().body(
                partyroomSetupQueryService.getSetupInfo(new PartyroomId(partyroomId)));
    }
}
